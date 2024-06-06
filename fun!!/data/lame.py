import matplotlib.pyplot as plt

file_path = "realsilly1k.txt"

def read_file(file_path):
    data = []
    with open(file_path, 'r') as file:
        for line in file:
            line = line.strip().strip('()')
            values = [float(v) for v in line.split(',')]
            data.append(tuple(values))
    return data

def plot_coordinates(data):
    plt.rcParams['font.family'] = 'Comic Sans MS'
    plt.rcParams['font.size'] = 14
    xs = [d[0] - 10 for d in data]
    ys = [d[1] for d in data]
    zs = [d[3] for d in data]
    plt.scatter(xs, ys, c=zs, cmap='viridis')

    plt.title('silly')
    plt.xlabel('subpixel')
    plt.ylabel('speed')
    plt.colorbar(label='frames')
    
    ax = plt.gca()
    ax.set_xlim([-0.5, 0.5])
    ax.set_ylim([0, 110])
    plt.subplots_adjust(left=0.2, right=0.7, top=0.9, bottom=.1) 
    
    plt.show()
    
data = read_file(file_path)
print(data)
plot_coordinates(data)