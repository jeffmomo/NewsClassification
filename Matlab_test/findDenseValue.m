function value = findDenseValue(vector)
    n = numel(vector);
    vector = sort(vector);
    KERNEL = range(vector) / 5;
    denseVector = zeros(1,n);
    for i = 1:n
        centerValue = vector(i);
        for j = linspace(-KERNEL,KERNEL,20)
            ind = vector>centerValue - j & vector<centerValue + j;
            denseVector(i) = denseVector(i) + sum(ind);
        end
    end
    maxInd = find(denseVector==max(denseVector));
    value = vector(maxInd);
    value = median(value);
end