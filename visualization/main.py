import pathlib
import sys
import numpy as np
import matplotlib.pyplot as plt
import pandas as pd
from scipy import stats


def main(argv):
    if len(argv) < 1:
        raise Exception(
            "Cantidad erronea de argumentos, respetar el siguiente órden:" + "\n" +
            "1- directorio de los archivos de log" + "\n"
        )

    # Args to local variables
    log_file_path = argv[0]

    distance_data, density_data = get_log_file(log_file_path)
    plot_distance(distance_data)
    plot_density(density_data)


def plot_distance(radius):
    p_lower = []
    p_avg = []
    p_upper = []
    keys = list(radius.keys())
    for k in keys:
        avg = np.average(radius[k])
        std = np.std(radius[k])

        p_lower.append(avg - std)
        p_avg.append(avg)
        p_upper.append(avg + std)

    plt.plot(keys, p_avg, color='magenta', marker="o", markersize=3, label="average distance")
    plt.fill_between(keys, p_lower, p_upper, color='red', alpha=0.2, label="distance within sd")

    key_index_min = 1
    key_index_max = int(len(keys) / 3) + 1

    slope, intercept, r_value, p_value, std_err = stats.linregress(keys[key_index_min:key_index_max], p_avg[key_index_min:key_index_max])
    lr_y = list(map(lambda x: x * slope + intercept, keys))
    plt.plot(keys, lr_y, alpha=0.6, color="black", label="alpha = {:0.4f}".format(slope))
    plt.plot([], [], alpha=0.0, label="r^2 = {:0.4f}".format(r_value**2))

    plt.xlabel('timestep', fontsize=14)
    plt.ylabel('distance to furthest particle', fontsize=14)
    plt.title('Distance to furthest particle over time', fontsize=18)

    plt.xlim(0)
    plt.grid(True)
    plt.legend(loc="best")

    plt.show()

def plot_density(density):
    d_lower = []
    d_avg = []
    d_upper = []
    keys = list(density.keys())
    for k in keys:
        avg = np.average(density[k])
        std = np.std(density[k])

        d_lower.append(avg - std)
        d_avg.append(avg)
        d_upper.append(avg + std)

    plt.plot(keys, d_avg, color='blue', marker="o", markersize=3, label="average density")
    plt.fill_between(keys, d_lower, d_upper, color='cyan', alpha=0.2, label="density within sd")

    plt.xlabel('timestep', fontsize=14)
    plt.ylabel('density (alive particles / volume)', fontsize=14)
    plt.title('Alive particles density over time', fontsize=18)

    plt.xlim(0)
    plt.grid(True)
    plt.legend(loc="upper right")

    plt.show()


def get_log_file(log_file_dir_path):
    log_files = []
    for path in pathlib.Path(log_file_dir_path).iterdir():
        if path.is_file():
            log_files.append(get_array_from_path(path))

    log_files = np.vstack(log_files)

    radius = {}
    density = {}
    for pair in log_files:
        if pair[0] not in radius:
            radius[pair[0]] = []
        if pair[0] not in density:
            density[pair[0]] = []
        radius[pair[0]].append(pair[1])
        density[pair[0]].append(pair[2])

    return radius, density


def get_array_from_path(log_file):
    df = pd.read_csv(log_file, delimiter="\t")
    return df.to_numpy()


if __name__ == '__main__':
    main(sys.argv[1:])
