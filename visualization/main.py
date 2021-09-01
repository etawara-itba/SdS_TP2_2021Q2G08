import pathlib
import sys
import numpy as np
import matplotlib.pyplot as plt

import pandas as pd


def main(argv):
    if len(argv) < 1:
        raise Exception(
            "Cantidad erronea de argumentos, respetar el siguiente órden:" + "\n" +
            "1- directorio de los archivos de log" + "\n"
        )

    # Args to local variables
    log_file_path = argv[0]

    files = get_log_file(log_file_path)
    plot(files)


def plot(radius):
    p_min = []
    p_avg = []
    p_max = []
    keys = radius.keys()
    for k in keys:
        p_min.append(np.min(radius[k]))
        p_avg.append(np.average(radius[k]))
        p_max.append(np.max(radius[k]))

    plt.plot(keys, p_avg, color='blue')
    plt.fill_between(keys, p_min, p_max, color='red', alpha=0.2)

    plt.xlabel('timestep', fontsize=15)
    plt.ylabel('distance to furthest particle', fontsize=15)
    plt.title('Distance to furthest particle in function of time')

    plt.grid(True)
    plt.show()


def get_log_file(log_file_dir_path):
    log_files = []
    for path in pathlib.Path(log_file_dir_path).iterdir():
        if path.is_file():
            log_files.append(get_array_from_path(path))

    log_files = np.vstack(log_files)

    radius = {}
    for pair in log_files:
        if pair[0] not in radius:
            radius[pair[0]] = []
        radius[pair[0]].append(pair[1])

    return radius


def get_array_from_path(log_file):
    df = pd.read_csv(log_file, delimiter="\t")
    return df.to_numpy()


if __name__ == '__main__':
    main(sys.argv[1:])
