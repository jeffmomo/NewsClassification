function [ output ] = testWithVoting( nets, data )
    % Run through all networks to get individual votes
    allOutputs = zeros(size(nets,2), size(data,2));
    for i = 1:size(nets,2)
        allOutputs(i,:) = nets{i}(data);
    end
    output = zeros(1,size(allOutputs,2));
    for p = 1:size(allOutputs,2)
        % Split voters into three groups and pick the group with the
        % lowest range between their outputs
        rows = 1:size(allOutputs,1);
        if (size(allOutputs,1) >= 6)
            groupRange = ceil(size(allOutputs,1)/3);
            gRange = zeros(1,3);
            lowerBound = [];
            higherBound = [];
            for j = 1:3
                lowerBound = [lowerBound groupRange*(j-1)+1];
                higherBound = [higherBound min(size(allOutputs,1),groupRange*j)];
                gRange(j) = range(allOutputs(lowerBound(j):higherBound(j), p));
            end
            group = find(gRange == min(gRange));
            rows = lowerBound(group):higherBound(group);
        end
        output(p) = findDenseValue(allOutputs(rows,p));
    end
    if size(nets,2) > 1
        output = [output ; range(allOutputs(rows,:))];
    else
        output = [allOutputs ; ones(1,size(data,2))];
    end
    figure;
    plot(1:numel(output(1,:)), output(1,:), '*', 'markerEdgeColor', 'black');
    hold on;
    A = reshape(allOutputs,numel(allOutputs),1);
    B = ((1:size(allOutputs,2))'*ones(1,size(allOutputs,1)))';
    B = reshape(B,numel(B),1);
    plot(B, A, '.', 'markerEdgeColor', 'black');
end

