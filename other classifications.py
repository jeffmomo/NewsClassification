import numpy as np
import matplotlib.pyplot as plt
from matplotlib.colors import ListedColormap
from sklearn.cross_validation import train_test_split
from sklearn.preprocessing import StandardScaler
from sklearn.datasets import make_moons, make_circles, make_classification
from sklearn.neighbors import KNeighborsClassifier
from sklearn.svm import SVC
from sklearn.tree import DecisionTreeClassifier
from sklearn.ensemble import RandomForestClassifier, AdaBoostClassifier
from sklearn.naive_bayes import GaussianNB, BernoulliNB
from sklearn.lda import LDA
from sklearn.qda import QDA
from random import shuffle
from sklearn import  grid_search




trainset_attr = []
trainset_class = []
test_attr = []
test_class = []


line_count = 0
maxcount_t = 1000
maxcount_test = 5000

f = open("training_set.txt", "r")
for line in f:
	if line_count == 0:
		bob = 1
	elif line_count < maxcount_t:
		datas = [float(x) for x in line[0:-1].split(",")]
		trainset_class.append(datas[-2])
		trainset_attr.append(datas[0:-3])
	line_count += 1
f.close()
print("training set loaded: " + str(len(trainset_class)))

line_count = 0

f = open("test_set.txt", "r")
for line in f:
	if line_count == 0:
		bob = 1
	elif line_count < maxcount_test:
		datas = [float(x) for x in line[0:-1].split(",")]
		test_class.append(datas[-2])
		test_attr.append(datas[0:-3])
	line_count += 1
f.close()
print("test set loaded:" + str(len(test_class)))


names = ["lsvm", "rbf svm", "decision tree", "naive bayes", "BernoulliNB"]

classifiers = [
SVC(kernel="linear", C=0.025),
SVC(),
DecisionTreeClassifier(),
GaussianNB(),
BernoulliNB()]

for name, clsf in zip(names, classifiers):
	print("classifying " + name + "...")
	clsf.fit(trainset_attr, trainset_class)

	baseline1 = 0
	baseline2 = 0
	baseline3 = 0

	correct = 0
	total = 0


	for i in range(0, len(test_attr)):
		actual = test_class[i]
		predicted = clsf.predict(test_attr[i])[0]

		if actual <= 25:
			baseline1 += 1
		elif actual <= 147:
			baseline2 += 1
		else:
			baseline3 += 1

		if actual <= 25 and predicted <= 25:
			correct += 1
		elif actual > 25 and actual <= 147 and predicted > 25 and predicted <= 147:
			correct += 1
		elif actual > 147 and predicted > 147:
			correct += 1
		total += 1

	print(correct, total)

	score = correct / total
	# score = clsf.score(test_attr, test_class)
	print("baseline score for {0}: {1}, {2}, {3}".format(name, baseline1 / total, baseline2 / total, baseline3 / total))
	print("score for {0}: {1}".format(name,score))

# parameters = {'kernel':('linear', 'rbf'), 'C':[1, 10, 100, 1000]}
# svr = SVC()
# clf = grid_search.GridSearchCV(svr, parameters)
# print(clf.fit(test_attr, test_class).best_score_)
