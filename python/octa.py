import socket

STX = bytes([2])  # or b'\x02'
ETX = bytes([3])  # or b'\x03'
EOT = bytes([4])  # or b'\x04'

message = STX + "Hello, server!".encode('utf-8') + ETX

with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
    s.connect(('localhost', 9099))
    s.sendall(message)



