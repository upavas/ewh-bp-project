import sys
import csv
    
def to_signed(n):
    return n - ((0x80 & n) << 1)


if __name__ == '__main__':
    mouse = file('/dev/input/mice')
    
   # bpWriter1 = csv.writer(open('bp-carlos-h1.csv', 'wb'), delimiter=',', quotechar='|', quoting=csv.QUOTE_MINIMAL)
    bpWriter2 = csv.writer(open('bp-marco-2.csv', 'wb'), delimiter=',', quotechar='|', quoting=csv.QUOTE_MINIMAL)
    while True:
        status, dx2, dy2 = tuple(ord(c) for c in mouse.read(3))
       # dx = to_signed(dx2)
       # dy = to_signed(dy2)
        # bpWriter.writerow(['dy'] * 5 + ['Baked Beans'])
       # bpWriter1.writerow([dx, dy])
        bpWriter2.writerow([status,dx2, dy2])
       # sys.stdout.write("h1: %#02x %d %d \n" % (status, dx, dy))
        sys.stdout.write("h2: %#02x %d %d \n" % (status, dx2, dy2))
        #sys.stdout.write(status +" "+dx +" "+ dy)
