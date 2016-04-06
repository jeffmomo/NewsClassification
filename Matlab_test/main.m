%% SETTINGS
HIGH_CONF = 0; % If 1, do not predict values with low confidence

warning('off','NNET:Obsolete');
%% LOAD NETWORKS FROM FILES
networksInDir = dir('NetworkNbr_*.mat');
networks = cell(1,numel(networksInDir));
for i = 1 : numel(networksInDir)
    tempStruct = load(networksInDir(i).name);
    networks{i} = tempStruct.net;
end
clear('networksInDir');
if sum(size(networks)) > 0 
    fprintf('Succesfullt loaded %d neurual networks from directory\n', size(networks,2));
else
    fprintf('ERROR: There is no neural networks availible in directory\n');
end

% LOAD TEST SET
testSet = csvread('Test_set.txt');
testInd = randsample(1:size(testSet,1), 5000);
testSet = testSet(testInd,:)';
testLabels = testSet(end,:);
testShareLabels = testSet(end-1,:);
testSet = testSet(1:end-2,:);

% Map buckets to shares
bucketValues = zeros(1,max(testLabels));
bucketValues(end) = max(testShareLabels);
for i = 1:numel(testShareLabels)
    bucketValues(testLabels(i)) = testShareLabels(i);
end
for i = 2:(numel(bucketValues)-1) % If there are unmarked buckets 
    if (bucketValues(i) < 1)         % (not in test set) interpolate values
        bucketValues(i) = (bucketValues(i-1) + bucketValues(i+1))/2;
    end
end

% Transform the data with std_parameters
load('std_parameters.mat');
testSet = trastd(testSet, trainSetMean, trainSetStd);

% Run test data through the networks to find predicted value and confidence
tic;
output = testWithVoting(networks, testSet);
plot(1:numel(testLabels), testLabels, 'o', 'markerEdgeColor', 'black');
legend('predicted value','Individual voters','Actual value');
xlabel('Pattern nbr');
ylabel('Belonging to bin');
t_predict = toc;
conf = output(2,:);
output = output(1,:);

%% PRESENT THE PREDICTIONS
dataArray = [testLabels' output' conf'];

% Standardize the confidency
aStd = std(dataArray(:,3));
aMean = mean(dataArray(:,3));
dataArray(:,3) = -(dataArray(:,3)-aMean)./aStd;
dataArray(dataArray(:,3)<-2,3) = -2;
if HIGH_CONF == 1 dataArray(dataArray(:,3)<mean(dataArray(:,3)),:) = []; end


figure('name', 'Confidence vs error rate');
plot(dataArray(:,3), abs(dataArray(:,2)-dataArray(:,1)),'.', 'MarkerEdgeColor', 'black');
xlabel('Confidence');
ylabel('Prediction error');

figure('name', 'Confidence distribution');
histogram(dataArray(:,3), 100, 'FaceColor', 'white', 'EdgeColor', 'black');
xlabel('Confidence - Higher is better');

dataArray = sortrows(dataArray,1);
figure('name','Prediction');
hold on;
plot(1:size(dataArray,1), dataArray(:,1),'black');
scatter(1:size(dataArray,1), dataArray(:,2),[], dataArray(:,3));
legend('Actual value','Predicted value');
xlabel('Pattern nbr');
ylabel('Belonging to bin');
colormap(flipud(gray(256)));
cb = colorbar;
ylabel(cb, 'Confidence - Higher is better');

% Split to three buckets for testing purpose
breakPoint1 = dataArray(ceil(end/3),1);
breakPoint2 = dataArray(ceil(end*2/3),1);
labelsBucket = zeros(size(dataArray,1),1);
outputBucket = zeros(size(dataArray,1),1);
for i = 1:size(dataArray,1)
    labelsBucket(i) = 2;
    if dataArray(i,1) < breakPoint1
        labelsBucket(i) = 1;
    end
    if dataArray(i,1) > breakPoint2
        labelsBucket(i) = 3;
    end
    outputBucket(i) = 2;
    if dataArray(i,2) < breakPoint1
        outputBucket(i) = 1;
    end
    if dataArray(i,2) > breakPoint2
        outputBucket(i) = 3;
    end
end
dataArray = [dataArray labelsBucket outputBucket];

figure('name','Predicted group');
plot(1:size(dataArray,1), dataArray(:,4), 'black');
hold on;
scatter(1:size(dataArray,1), dataArray(:,5),[], dataArray(:,3), 'MarkerEdgeColor', 'black');
legend('Actual group','Predicted Group');

grayColor = [0.5 0.5 0.5];
figure('name','Predicted groups for each correct group');
spa = subplot(1,3,1);
histogram(dataArray(dataArray(:,4)==1,5),3, 'FaceColor', grayColor, 'EdgeColor', 'black');
set(gca, 'XTickLabel',{'Class 1', 'Class 2', 'Class 3'}, 'XTick',1:3)
title(spa, sprintf('Group 1: y < %d shares', bucketValues(breakPoint1)));
xlabel('Predicted group');
spb = subplot(1,3,2);
histogram(dataArray(dataArray(:,4)==2,5),3, 'FaceColor', grayColor, 'EdgeColor', 'black');
set(gca, 'XTickLabel',{'Class 1', 'Class 2', 'Class 3'}, 'XTick',1:3)
title(spb, sprintf('Group 2: %d < y < %d shares', bucketValues(breakPoint1), bucketValues(breakPoint2)));
xlabel('Predicted group');
spc = subplot(1,3,3);
histogram(dataArray(dataArray(:,4)==3,5),3, 'FaceColor', grayColor, 'EdgeColor', 'black');
set(gca, 'XTickLabel',{'Class 1', 'Class 2', 'Class 3'}, 'XTick',1:3)
title(spc, sprintf('Group 3: %d < y shares', bucketValues(breakPoint2)));
xlabel('Predicted group');

correctGroup = sum(dataArray(:,4) == dataArray(:,5));
predictionAccuracity = correctGroup / size(dataArray,1);
largestGroupAccuracity = sum(dataArray(:,4)==median(dataArray(:,4))) / numel(dataArray(:,4));
gain = (predictionAccuracity-largestGroupAccuracity)*100;
fprintf('TOTAL\tPrediction Accuracy: %.1f%%, Largest group accuracy: %.1f%%, Gain: %.1f%%\n',predictionAccuracity*100, largestGroupAccuracity*100, gain);

% Class based accuracy
correctGroup = zeros(1,3);
wrongGroup = zeros(1,3);
correctClassifiedData = dataArray(dataArray(:,4)==dataArray(:,5),4);
for i = 1:3
    correctGroup(i) = sum(correctClassifiedData==i);
    predictionAccuracityI = correctGroup(i) / sum(dataArray(:,4)==i);
    fprintf('Group %d\tPrediction Accuracy: %.1f%%\n', i, predictionAccuracityI*100);
end

% TEST EFFECT OF CATEGORY
figure('name', 'Effect of category');
catDec = [0 2.^(0:7)];
catOut = zeros(1,8);
catNoneOut = 0;
nbrIterations = 100;
for it = 1:nbrIterations
    for i = 1:numel(catDec)
        catBin = de2bi(catDec(i), 8);
        testPattern = testSet(:,ceil(rand*(size(testSet,2)-1)));
        testPattern(1:8) = catBin';
        if catDec(i) == 0
            catNoneOut = catNoneOut + networks{1}(testPattern);
        else
            catOut(i-1) = catOut(i-1) + networks{1}(testPattern);
        end
    end
end
catNoneOut = catNoneOut / nbrIterations;
catOut = catOut / nbrIterations;
bar(1:numel(catOut),catOut-catNoneOut, 'FaceColor', 'white', 'EdgeColor', 'black');
xAxisString = {'Asia','Business','Lifestyle','Politics','Singapore','Sport','Tech','World'};
set(gca, 'XTickLabel',xAxisString, 'XTick',1:numel(xAxisString))