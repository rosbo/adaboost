Adaboost
==========

We implement two different types of classifier:
- Naive Bayesian Classifier (NBC)
- Decision Tree Classifier (DTC)

Usage
---------

 - f <FILENAME>     The name of the data file
 - m <MAX>          Max number of different values per feature
 - n <NBC DTC>      Number of NBC and DTC
 - p <PERCENTAGE>   The percentage of the data set to be used for training
 - d <DEPTH>        The maximum depth for the DTCs [Default: 0 which means maximum possible depth]
 
If you want to work with the page-blocks dataset, use 10 NBC and 10 DTC(with a max depth of 2) and split the use 80% of the dataset as a training set and the other 20% as a test set, type de following parameters:
    
    -f datasets/page-blocks.txt -m 50 -n 10 0 -p 80 -d 2
