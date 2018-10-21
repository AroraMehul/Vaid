# VAIDYA

Basic Information : This app is based on the theme that a rural people need their queries to be answered and they could be helped in other ways. This app was built keeping in mind the reluctance of acceptance of technology and illiteracy or not able to read or write or both.
On The first page of the app, the app expects the user to click a photo of his/her Aadhaar Card, our app automatically extracts aadhaar number from the image and signs in the user with aadhaar number as credential. With the aadhaar number, all the basic information and medical history can be extracted and used.
Next the user is supposed to enter his/her query for a doctor as an audio or a text input depending on his/her convinience. The user can also upload an image if he/she wants to (for example if he/she catches some skin disease). All this data is stored in unique folders each for one user which will then be transfered to appropriate doctor.
Then, the user is prompted to select symptoms he/she is suffering from among 100-200 symptoms. This information is then processed on our server to return 3 possible diseases that he/she might be suffering from, The server contains a database of diseases with their symptoms and number of occurrences( file name raw_data.xlsx, which is being pre-processed before use).
The user also gets an Estimated time in which his/her query will get responded from the app, for that we have trained our deep learning model on a self-generated dataset on features like disease, time at which query was sent, number of doctos available and number of pending requests. The actual response time from the doctor will be recorded and will help in building an actual dataset, the model will be re-trained after specific amount of time.
Finally the user is taken to a summary page where he is displayed what symptoms he/she had chosen, probable diseases, estimated time of response.
When the doctor responds to the query, a chat is being established between the patient and the doctor

#CONTRIBUTERS: Dhruv Agarwal, Shivansh Beohar, Mehul Arora, Ritik Mahajan
