import pika
import random
import time

class PressureGenerationClient:
    def __init__(self):
        self.connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
        self.channel = self.connection.channel()
        self.channel.queue_declare(queue='pressureQueue')


    def start_sending(self):
        while True:
            measurement = random.randint(0,10)
            self.channel.basic_publish(exchange='', routing_key='pressureQueue', body=str(measurement).encode())
            print(f'Sent measurement: {measurement}')
            time.sleep(4)


    def close_connection(self):
        self.connection.close()

if __name__ == '__main__':
    sender = PressureGenerationClient()
    try:
        sender.start_sending()
    except KeyboardInterrupt:
        sender.close_connection()