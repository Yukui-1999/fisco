import pandas as pd
import numpy as np
import pickle
import matplotlib.pyplot as plt
from scipy import stats
import tensorflow as tf
from tensorflow import keras
import seaborn as sns
from pylab import rcParams
from sklearn import metrics
from sklearn.model_selection import train_test_split
import warnings
import os
for dirname, _, filenames in os.walk('data'):
    for filename in filenames:
        print(os.path.join(dirname, filename))
def get_features_from_data(df):
    for (ci,col) in enumerate(df.columns):
        if col.startswith('label'):
            first_label_ind = ci;
            break;
    feature_names = df.columns[1:first_label_ind]; # 把label前，时间戳之后的列截取出来，就是features的所有列
    return np.array(feature_names)
# 获取传感器名
def get_sensor_names_from_features(feature_names):
    feat_sensor_names = np.array([None for feat in feature_names]);
    for (fi,feat) in enumerate(feature_names):
        if feat.startswith('a'):
            feat_sensor_names[fi] = 'Acc'; # 加速度 3列
            pass;
        elif feat.startswith('w'):
            feat_sensor_names[fi] = 'Gyro'; # 角速度 3列
            pass;
        elif feat.startswith('A'):
            feat_sensor_names[fi] = 'Angle'; # 欧拉角 3列
            pass;
        elif feat.startswith('q'):
            feat_sensor_names[fi] = 'Quaternion'; # 四元数 4列
            pass;
        elif feat.startswith('T(°)'):
            feat_sensor_names[fi] = 'Temperature'; # 欧拉角 3列
            pass;
        elif feat.startswith('Vol'):
            feat_sensor_names[fi] = 'Voltage'; # 四元数 4列
            pass;
        else:
            continue
    #             raise ValueError("!!! Unsupported feature name: %s" % feat);
    return feat_sensor_names;
def estimate_standardization_params(X):
    with warnings.catch_warnings():
        warnings.simplefilter("ignore", category=RuntimeWarning)
        mean_vec = np.nanmean(X,axis=0);
        std_vec = np.nanstd(X,axis=0);
        return (mean_vec,std_vec);

def standardize_features(X,mean_vec,std_vec):
    # Subtract the mean, to centralize all features around zero:
    X_centralized = X - mean_vec.reshape((1,-1));
    # Divide by the standard deviation, to get unit-variance for all features:
    # * Avoid dividing by zero, in case some feature had estimate of zero variance
    normalizers = np.where(std_vec > 0., std_vec, 1.).reshape((1,-1));
    X_standard = X_centralized / normalizers;
    return X_standard;
def project_features_to_selected_sensors(feature_names,sensors_to_use):

    feature_names_arr = []
    for sensor in set(sensors_to_use):
        if sensor == 'Acc':
            for feature in feature_names:
                #print (type(feature))
                if (feature.startswith('a')):
                    feature_names_arr.append(feature)
        elif sensor == 'Gyro':
            for feature in feature_names:
                if (feature.startswith('w')):
                    feature_names_arr.append(feature)
        elif sensor == 'Angle':
            for feature in feature_names:
                if (feature.startswith('A')):
                    feature_names_arr.append(feature)
        elif sensor == 'Quaternion':
            for feature in feature_names:
                if (feature.startswith('q')):
                    feature_names_arr.append(feature)

    return feature_names_arr
def get_label_names(df):
    # Search for the column of the first label:
    for (ci,col) in enumerate(df.columns):
        if col.startswith('label:'):
            first_label_ind = ci;
            break;
        pass;

    label_names = np.array(df.columns[first_label_ind:]);
    #     for (li,label) in enumerate(label_names):
    #         # In the CSV the label names appear with prefix 'label:', but we don't need it after reading the data:
    #         assert label.startswith('label:');
    #         #label_names[li] = label.replace('label:','');
    #         pass;

    return (list(label_names));
def prepare_X_Y_for_ML(df):
    # prepare data for machine learning
    # 1. get all features available
    feature_names = get_features_from_data(df)

    # 2. get the features sensors feat from features
    #     feat_sensor_names = get_sensor_names_from_features(feature_names);

    # 3. select the sensors to use in the machine learning
    sensors_to_use = ['Acc','Gyro', 'Angle', 'Quaternion'];

    # 4. get Data accoring to selected sensors with feaures;
    feature_names_arr = []
    feature_names_arr = project_features_to_selected_sensors(feature_names, sensors_to_use)
    X = df[feature_names_arr]
    #     print(type(X)) X is DataFrame
    # 5. standardize the features substracting the mean value and dividing by standard deviation
    # so that all their values will be roughly in the same range:
    (mean_vec,std_vec) = estimate_standardization_params(X);
    X = standardize_features(X,mean_vec,std_vec);
    #     X[np.isnan(X)] = 0.
    # 6. X is ready for training
    # 7. Prepare Y target lables for training
    label_names = get_label_names(df)
    Y = df[label_names]
    # 8. clean nan values and converted to binary labels
    # Read the binary label values, and the 'missing label' indicators:
    trinary_labels_mat = df[label_names]; # This should have values of either 0., 1. or NaN
    #     M = np.isnan(trinary_labels_mat); # M is the missing label matrix
    #     Y = np.where(M,0,trinary_labels_mat) > 0.; # Y is the label matrix
    #     print(Y)
    y_df = pd.DataFrame(Y)
    #     y_df.rename(columns=dict(enumerate(label_names, 0)), inplace = True)
    return (X,y_df,feature_names,label_names)
import glob as glob

csv_files = glob.glob("data/*.csv")

list_user_files = []

for file in csv_files:
    user_data = pd.read_csv(file)
    list_user_files.append(user_data)


all_users_data = pd.concat(list_user_files, axis=0, ignore_index=True)
(X_all,Y_all,feature_names,label_names) = prepare_X_Y_for_ML(all_users_data)
num_train_samples = int(0.6 * len(X_all))
num_val_samples = int(0.2 * len(X_all))
num_test_samples = len(X_all) - num_train_samples - num_val_samples
print(num_train_samples, num_val_samples, num_test_samples)
sequence_length = 150
batch_size = 256

train_dataset = keras.utils.timeseries_dataset_from_array(
    data=X_all,
    targets=Y_all['label:rotate_direction'],
    sequence_length=sequence_length,
    batch_size=batch_size,
    start_index=0,
    end_index=num_train_samples)

val_dataset = keras.utils.timeseries_dataset_from_array(
    data=X_all,
    targets=Y_all['label:rotate_direction'],
    sequence_length=sequence_length,
    batch_size=batch_size,
    start_index=num_train_samples,
    end_index=num_train_samples + num_val_samples)

test_dataset = keras.utils.timeseries_dataset_from_array(
    data=X_all,
    targets=Y_all['label:rotate_direction'],
    sequence_length=sequence_length,
    batch_size=batch_size,
    start_index=num_train_samples + num_val_samples)
for samples, targets in train_dataset:
    print("samples shape:", samples.shape)
    print("targets shape:", targets.shape)
    break
from keras import layers
# 尝试训练一个LSTM层

inputs = keras.Input(shape=(sequence_length, X_all.shape[-1]))
# x = layers.LSTM(32,recurrent_dropout=0.25)(inputs)
# x = layers.Dropout(0.5)(x)
x = layers.LSTM(32,recurrent_dropout=0.25)(inputs)

outputs = layers.Dense(units=1, activation="sigmoid")(x)
model = keras.Model(inputs, outputs)

callbacks = [
    keras.callbacks.ModelCheckpoint("LSTM.h5",
                                    save_best_only=True)
]
model.result.csvcompile(optimizer="adam", loss="binary_crossentropy", metrics=["accuracy"])
history = model.fit(train_dataset,
                    epochs=50,
                    validation_data=val_dataset,
                    callbacks=callbacks)
model = keras.models.load_model("LSTM.h5")
print(f"Test Acc: {model.evaluate(test_dataset)[1]:.3f}")