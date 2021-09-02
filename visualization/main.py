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

    distance_data, density_data = get_log_file(log_file_path)
    plot_distance(distance_data)
    plot_density(density_data)


def plot_distance(radius):
    p_min = []
    p_avg = []
    p_max = []
    keys = radius.keys()
    for k in keys:
        p_min.append(np.min(radius[k]))
        p_avg.append(np.average(radius[k]))
        p_max.append(np.max(radius[k]))

    plt.plot(keys, p_avg, color='magenta')
    plt.fill_between(keys, p_min, p_max, color='red', alpha=0.2)

    plt.xlabel('timestep', fontsize=15)
    plt.ylabel('distance to furthest particle', fontsize=15)
    plt.title('Distance to furthest particle in function of time')

    plt.xlim(0)
    plt.grid(True)

    plt.show()

def plot_density(density):
    d_min = []
    d_avg = []
    d_max = []
    keys = density.keys()
    for k in keys:
        d_min.append(np.min(density[k]))
        d_avg.append(np.average(density[k]))
        d_max.append(np.max(density[k]))

    plt.plot(keys, d_avg, color='blue')
    plt.fill_between(keys, d_min, d_max, color='cyan', alpha=0.2)

    plt.xlabel('timestep', fontsize=15)
    plt.ylabel('density (alive particles / volume)', fontsize=15)
    plt.title('Alive particles density over time')
    plt.xlim(0)
    plt.grid(True)

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
