要进行kmeans和dbscan聚类，可以直接运行l2r.process下的SampleReader.java文件，该类实现了读取samples并
对数据预处理（特征向量的归一化、删除包含document过多或过少的samples、删除平均特征值过大的sample），在main函数
中分别调用kmeans和dbscan方法进行聚类。