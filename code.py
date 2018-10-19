import csv
import pandas as pd
import numpy as np
from collections import defaultdict
import seaborn as sns
import matplotlib.pyplot as plt
from sklearn.externals import joblib

df = pd.read_excel('raw_data.xlsx')

data = df.fillna(method='ffill')
data.head()

# Process Disease and Symptom Names
def process_data(data):
    data_list = []
    data_name = data.replace('^','_').split('_')
    n = 1
    for names in data_name:
        if (n % 2 == 0):
            data_list.append(names)
        n += 1
    return data_list

disease_list = []
disease_symptom_dict = defaultdict(list)
disease_symptom_count = {}
count = 0

for idx, row in data.iterrows():
    
    # Get the Disease Names
    if (row['Disease'] !="\xc2\xa0") and (row['Disease'] != ""):
        disease = row['Disease']
        disease_list = process_data(data=disease)
        count = row['Count of Disease Occurrence']

    # Get the Symptoms Corresponding to Diseases
    if (row['Symptom'] !="\xc2\xa0") and (row['Symptom'] != ""):
        symptom = row['Symptom']
        symptom_list = process_data(data=symptom)
        for d in disease_list:
            for s in symptom_list:
                disease_symptom_dict[d].append(s)
            disease_symptom_count[d] = count
        
# See that the data is Processed Correctly
disease_symptom_dict

# Count of Disease Occurence w.r.t each Disease
disease_symptom_count

df1 = pd.DataFrame(list(disease_symptom_dict.items()), columns=['Disease', 'Symptom'])

diseases = df1['Disease']

############################################ GENERATING DATASET ################################
import random

l=[]
for i in range(10000):
    l.append([])
    
    
maxi = 0
mini = 1000000000000
for i in range(10000):
    l[i].append(diseases[random.randint(0,148)])
    l[i].append(random.randint(0,23))
    l[i].append(random.randint(0,59))
    l[i].append(random.randint(0,59))
    l[i].append(random.randint(0,20))
    l[i].append(random.randint(0,100))
    l[i].append((l[i][1]-11)*(l[i][1]-19) + 20 + l[i][2]**2 + l[i][3] + 160000 - l[i][4] + l[i][5]**3/1000.0 + ((l[i][1] - 11)*(l[i][1] - 19) + 20 + l[i][2]**2 + l[i][3])*(160000 - l[i][4]**4)*(l[i][5]**3) + random.randint(0,148)**4/10000.0 + (random.randint(0,148)**4/10000.0)*((l[i][1] - 11)*(l[i][1] - 19) + 20 + l[i][2]**2 + l[i][3])*(160000 - l[i][4]**4)*(l[i][5]**3))
    maxi = max(l[i][6], maxi)
    mini = min(l[i][6], mini)
    
for i in range(10000):
    l[i][6] = (l[i][6] - mini)/(maxi - mini)*5.0
    
train_data = pd.DataFrame(l, columns = ['Disease', 'hrs', 'mins', 'secs', 'num_docs', 'num_pen_req', 'time_taken'])

################################## DL MODEL ################################################

y = train_data['time_taken']
train_data.drop(['time_taken'], axis = 1, inplace = True)

from sklearn.preprocessing import StandardScaler,OneHotEncoder,LabelEncoder
from keras.models import Sequential
from keras.layers import Dense, Dropout

le=LabelEncoder()
train_data['Disease']=le.fit_transform(train_data['Disease'])
oe=OneHotEncoder(categorical_features=[0])
train_data=oe.fit_transform(train_data).toarray()
sc = StandardScaler()
train_data = sc.fit_transform(train_data)
joblib.dump(sc,"scaler.save")
joblib.dump(le,"label.save")
joblib.dump(oe,"onehot.save")



model = Sequential()

model.add(Dense(70, kernel_initializer = 'uniform', activation = 'relu', input_shape=(154,)))
model.add(Dropout(0.5))
model.add(Dense(30, kernel_initializer = 'uniform', activation = 'relu'))
model.add(Dropout(0.4))
model.add(Dense(10, kernel_initializer = 'uniform', activation = 'relu'))
model.add(Dropout(0.3))
model.add(Dense(1, activation = 'relu'))

model.compile(optimizer = "adam", loss = "mean_squared_error", metrics = ['mse'])

model.fit(train_data, y, epochs = 100, batch_size = 64)

model.save_weights("weights.h5")
model.save("model.h5")

############################# TEST ##############################################
import pandas as pd
test_data = pd.DataFrame([['anemia', '17', 1, 1, 50, 500]], columns = ['Disease', 'hrs', 'mins', 'secs', 'num_docs', 'num_pen_req'])
test_data['Disease'] = le.transform(test_data['Disease'])
test_data = oe.transform(test_data).toarray()
test_data = sc.transform(test_data)
x=model.predict(test_data)