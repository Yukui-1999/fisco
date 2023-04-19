import pandas as pd
import numpy as np
import sys
import tensorflow as tf
from collections import Counter
from tensorflow import keras
import warnings
warnings.filterwarnings("ignore")
if __name__ == '__main__':
    filepath=sys.argv[1]
    print(filepath)
    # filepath='/Users/dingzhengyao/fisco/ZS01/src/main/java/org/fisco/bcos/asset/client/data/result.csv'
    all_data=pd.read_csv(filepath)
    # cancel time
    feature=all_data.columns[1:14]
    X=all_data[feature]

    # scale maxmin
    for i in X.columns:
        X[i] = (X[i]-X[i].min())/(X[i].max()-X[i].min())

    N_TIME_STEPS = 150 # (correspond to 3-second window)
    step = 15 # 每个时间窗口之间的间隔
    segments = []

    for i in range(0, len(X) - N_TIME_STEPS, step):
        ax = X['ax(g)'].values[i: i + N_TIME_STEPS]
        ay = X['ay(g)'].values[i: i + N_TIME_STEPS]
        az = X['az(g)'].values[i: i + N_TIME_STEPS]
        wx = X['wx(deg/s)'].values[i: i + N_TIME_STEPS]
        wy = X['wy(deg/s)'].values[i: i + N_TIME_STEPS]
        wz = X['wz(deg/s)'].values[i: i + N_TIME_STEPS]
        AngleX = X['AngleX(deg)'].values[i: i + N_TIME_STEPS]
        AngleY = X['AngleY(deg)'].values[i: i + N_TIME_STEPS]
        AngleZ = X['AngleZ(deg)'].values[i: i + N_TIME_STEPS]
        q0 = X['q0'].values[i: i + N_TIME_STEPS]
        q1 = X['q1'].values[i: i + N_TIME_STEPS]
        q2 = X['q2'].values[i: i + N_TIME_STEPS]
        q3 = X['q3'].values[i: i + N_TIME_STEPS]

        segments.append([ax, ay, az, wx, wy, wz, AngleX, AngleY, AngleZ, q0, q1, q2, q3])

    reshaped_segments = np.asarray(segments, dtype=np.float32).reshape(-1, N_TIME_STEPS, 13)

    model = tf.keras.models.load_model("/Users/dingzhengyao/fisco/ZS01/src/main/java/org/fisco/bcos/asset/client/model/transformer_1head1block.keras")
    returns=model.predict(reshaped_segments)
    returnarr=[]
    for i in range(len(returns)):
        maxindex=np.argmax(returns[i])
        returnarr.append(maxindex)
    counts = Counter(returnarr)
    most_common = counts.most_common(1)
    print(most_common[0][0]+1)
