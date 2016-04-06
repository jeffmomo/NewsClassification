for nbr = 1:21
        %% TRAIN NETWORK
        trainSet = csvread('Training_set.txt');
        trainSet = trainSet';
        trainLabelsShare = trainSet(end-1,:);
        trainLabels = trainSet(end,:);
        trainSet = trainSet(1:end-2,:);
        
        % Standardize the data set
        [trainSet, meanData, stdData] = prestd(trainSet);
        
        %% CROP DATA SET
        % DEPENDING ON MACHINE MEMORY HEAVY CROPING
        % MIGHT BE NECCESSARY
        %%trainSet = trainSet(:,1:5000);
        %%trainLabels = trainLabels(:,1:5000);

        % Add disturbance to data (48 -> 50.5%)
        trainSet2 = trainSet;
        trainLabels2 = trainLabels;
        for i = 1:3
            trainSet = [trainSet (trainSet2 + (rand(size(trainSet2))-0.5)*0.05)];
            trainLabels = [trainLabels trainLabels2];
        end
        clear('trainSet2');
        clear('trainLabels2');

        net = fitnet([ceil(50*(rand*0.5+0.5)) ceil(20*(rand*0.5+0.5)) 5]);
        %net.trainFcn = 'trainscg';
        net.trainParam.lr = 0.01 + rand*0.1;
        net = train(net, trainSet, trainLabels);%, 'useGPU', 'yes', 'showResources', 'yes');
        
        save(sprintf('NetworkNbr_%d',nbr),'net')
end