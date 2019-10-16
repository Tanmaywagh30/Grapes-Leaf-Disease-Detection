from keras.models import Sequential
from keras.layers.core import Dense, Dropout, Activation, Flatten
from keras.layers.convolutional import Convolution2D, MaxPooling2D
from keras.optimizers import SGD, RMSprop, adam
from keras.utils import np_utils
import numpy as np
import matplotlib.pyplot as plt
import os
from PIL import Image
from numpy import *
from sklearn.utils import shuffle
from keras.models import Sequential
from keras.layers.core import Dense, Dropout, Activation, Flatten
from keras.layers.convolutional import Convolution2D, MaxPooling2D
from keras.optimizers import SGD, RMSprop, adam
from keras.utils import np_utils
import numpy as np
import matplotlib.pyplot as plt
import os
import theano
from PIL import Image
from numpy import *
from sklearn.utils import shuffle
from sklearn.model_selection import train_test_split

path0 = "C:\\Users\\HP\\Desktop\\Grapes Dataset\\t6"
path1 = "C:\\Users\\HP\\Desktop\\Grapes Dataset\\Bacterial spots"
path2 = "C:\\Users\\HP\\Desktop\\Grapes Dataset\\Downey Mildew"
path3 = "C:\\Users\\HP\\Desktop\\Grapes Dataset\\Normal"
path4 = "C:\\Users\\HP\\Desktop\\Grapes Dataset\\Powdery Mildew"
path5 = "C:\\Users\\HP\\Desktop\\Grapes Dataset\\Rust"

# myfun(os.listdir(path5),path5)
# myfun(os.listdir(path2),path2)
# myfun(os.listdir(path3),path3)
#
# myfun(os.listdir(path1),path1)

listing = os.listdir(path0)
immatrix = array([array(Image.open(path0+"\\"+gray_data)).flatten()for gray_data in listing],'f')

label = np.ones((70),dtype=int)
label[0:35] = 0
label[35:] = 1

data, Label = shuffle(immatrix,label)
train_data = [data,Label]

(X,y) = (train_data[0],train_data[1])
X_train,X_test,Y_train,Y_test = train_test_split(X,y,test_size=0.1)
X_train= X_train.reshape(X_train.shape[0],227,227,3)
X_test= X_test.reshape(X_test.shape[0],227,227,3)

X_train = X_train.astype('float32')
X_test = X_test.astype('float32')

X_train = X_train/255
X_test = X_test/255

Y_train = np_utils.to_categorical(Y_train,2)
Y_test = np_utils.to_categorical(Y_test,2)


model = Sequential()
model.add(Convolution2D(filters=96,input_shape=(227,227,3),kernel_size=(11,11),strides=(4,4),padding="valid"))
model.add(Activation('relu'))
model.add(MaxPooling2D(pool_size=(3,3),strides=(2,2),padding='valid'))


model.add(Convolution2D(filters=256,kernel_size=(5,5),strides=(1,1),padding='valid'))
model.add(Activation('relu'))
model.add(MaxPooling2D(pool_size=(3,3),strides=(2,2),padding='valid'))

model.add(Convolution2D(filters=384,kernel_size=(3,3),strides=(1,1),padding='valid'))
model.add(Activation('relu'))

model.add(Convolution2D(filters=384,kernel_size=(3,3),strides=(1,1),padding='valid'))
model.add(Activation('relu'))

model.add(Convolution2D(filters=384,kernel_size=(3,3),strides=(1,1),padding='valid'))
model.add(Activation('relu'))
model.add(MaxPooling2D(pool_size=(3,3), strides=(2,2), padding='valid'))

model.add(Flatten())
model.add(Dense(9216))
model.add(Activation('relu'))
model.add(Dropout(0.4))
#
# model.add(Dense(4096))
# model.add(Activation('relu'))
model.add(Dense(4096))
model.add(Activation('relu'))
model.add(Dropout(0.4))

model.add(Dense(1000))
model.add(Activation('relu'))
model.add(Dropout(0.4))

model.add(Dense(1000))
model.add(Activation('relu'))
model.add(Dropout(0.4))


model.add(Dense(2))
model.add(Activation('softmax'))

model.compile(loss='categorical_crossentropy',optimizer='adadelta',metrics=['accuracy'])

model.fit(X_train,Y_train,batch_size=16,epochs=30,verbose=1,validation_data=(X_test,Y_test))

print("Predicted:")
print(model.predict_classes(X_test[1:10]))

print("Actual")
print("\n",Y_test[1:10])
model.save("t4.hdf5")



