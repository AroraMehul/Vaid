import csv
import pandas as pd
import numpy as np
from collections import defaultdict
import seaborn as sns
import matplotlib.pyplot as plt

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

df1 = pd.DataFrame(list(disease_symptom_dict.items()), columns=['Disease','Symptom'])
df1.head()

for vals in disease_symptom_count.items():
    print(vals[1])
    
df1["Number_of_Occurences"]=0

for i in range(len(df1)):
    df1["Number_of_Occurences"][i] = disease_symptom_count[df1["Disease"][i]]
    


#######################################################################################################################
    
#PREDICTION
prediction = []

input_symptoms = ['hematuria']

for i in range(len(df1)):
    flag=1
    for j in range(len(input_symptoms)):
        symp = input_symptoms[j]
        if symp not in df1["Symptom"][i]:
            flag = 0
            break
    if flag==1:
        prediction.append(df1["Disease"][i])


#COMMON ELEMENTS
max_n = 0
dis1=""
dis2=""
for i in range(len(df1)):
    for j in range(i+1, len(df1)):
        list1 = df1["Symptom"][i]
        list2 = df1["Symptom"][j]
        if len(list(set(list1).intersection(list2))) > max_n:
            dis1 = df1["Disease"][i]
            dis2 = df1["Disease"][j]
            max_n = len(list(set(list1).intersection(list2)))
        
        
while(len(prediction) > 3):
    symp = ""
    for i in range(len(prediction)):
        for j in range(len(disease_symptom_dict[prediction[i]])):
            if disease_symptom_dict[prediction[i]][j] not in input_symptoms:
               symp =  disease_symptom_dict[prediction[i]][j]
               break
        if(symp!=""):
            break
    
    ############################# ASK USER ABOUT THIS SYMPTOM #########################
    ############################ 0 for not pressent, 1 for present and 2 for not sure #######
    print("")
    symptom_present = int(input("IS "+symp+" PRESENT"))
    input_symptoms.append(symp)
    
    if symptom_present == 0:
        for i in range(len(prediction)):
            if symp in disease_symptom_dict[prediction[i]]:
                prediction[i] = ""
                
    elif symptom_present == 1:
        for i in range(len(prediction)):
            if symp not in disease_symptom_dict[prediction[i]]:
                prediction[i] = ""
    
    elif symptom_present == 2:
        pass
    
    else:
        print("provide valid input")
    
    temp = []
    for i in range(len(prediction)):
        if prediction[i]!="":
            temp.append(prediction[i])
    
    prediction = temp
    

print(prediction)