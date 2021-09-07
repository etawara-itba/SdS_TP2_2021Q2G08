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
            "1- directorio de los archivos de log" + "\n" +
            "2- regla (opcional, default=CLASSIC)" + "\n" +
            "3- porcentaje inicial (opcional, default=0.3)" + "\n"
        )

    # Args to local variables
    log_file_path = argv[0]
    rule = argv[1] if len(argv) > 2 else "CLASSIC"
    ratio = argv[2] if len(argv) > 3 else "0.3"

    log_data, distance_data, density_data = get_log_file(log_file_path)

    slope, intercept, r_value = compute_alpha(log_data, rule, ratio)

    plot_distance(distance_data, slope, intercept, r_value)
    plot_density(density_data)
    plot_historical_alpha(rule)


def plot_distance(radius, slope, intercept, r_value):
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

    lr_y = list(map(lambda x: x * slope + intercept, keys))
    plt.plot(keys, lr_y, alpha=0.6, color="black", label="alpha = {:0.4f}".format(slope))
    plt.plot([], [], alpha=0.0, label="r^2 = {:0.4f}".format(r_value ** 2))

    plt.xlabel('timestep', fontsize=20)
    plt.ylabel('distance to furthest particle', fontsize=20)
    plt.tick_params(axis='both', which='major', labelsize=20)

    plt.xlim(0)
    plt.grid(True)
    plt.legend(loc="best", prop={"size": 20})

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

    plt.xlabel('timestep', fontsize=20)
    plt.ylabel('density (alive particles / volume)', fontsize=20)
    plt.tick_params(axis='both', which='major', labelsize=20)

    plt.xlim(0)
    plt.grid(True)
    plt.legend(loc="upper right", prop={"size": 20})

    plt.show()


def compute_alpha(log_data, rule, ratio, log_dir="../logging"):
    x = log_data[:, 0]
    y = log_data[:, 1]
    slope, intercept, r_value, p_value, std_err = stats.linregress(x, y)

    pathlib.Path(log_dir).mkdir(parents=True, exist_ok=True)
    with open(log_dir + "/alpha.log", "a") as f:
        f.write("{:s}\t{:s}\t{:0f}\t{:0f}\n".format(rule, ratio, slope, std_err))

    return slope, intercept, r_value


def plot_historical_alpha(rule, log_dir="../logging"):
    alphas = get_filtered_array(log_dir + "/alpha.log", rule)
    x = alphas[:, 0]
    center = alphas[:, 1]
    spread = alphas[:, 2]

    violin_data = []
    for i in range(len(x)):
        violin_data.append([center[i], center[i] - spread[i], center[i] + spread[i]])

    parts = plt.violinplot(violin_data, x, widths=0.05, showmeans=True)
    for pc in parts['bodies']:
        pc.set_alpha(0)

    plt.plot(x, center, marker="o", alpha=0.6, color="green")

    plt.xlabel('Initial alive population ratio', fontsize=20)
    plt.ylabel('Alpha', fontsize=20)
    plt.tick_params(axis='both', which='major', labelsize=20)
    plt.ticklabel_format(useMathText=True)

    plt.xlim(0, 1)
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

    return log_files, radius, density


def get_array_from_path(log_file):
    df = pd.read_csv(log_file, delimiter="\t")
    return df.to_numpy()


def get_filtered_array(alpha_file, rule):
    df = pd.read_csv(alpha_file, delimiter="\t", header=None)
    df = df[df[0] == rule]
    df = df.drop_duplicates(subset=[1], keep="last")
    del df[0]
    return df.to_numpy()


if __name__ == '__main__':
    main(sys.argv[1:])
