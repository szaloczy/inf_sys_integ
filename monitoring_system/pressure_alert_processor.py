import pika

class PressureAlertProcessor:
    def __init__(self):
        self.connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
        self.channel = self.connection.channel()
        self.channel.queue_declare(queue='pressureQueue')
        self.channel.queue_declare(queue='pressureAlertQueue')
        self.high_pressure_counter = 0


    def process_measurement(self, measurement: int):
        if measurement >= 8:
            self.high_pressure_counter += 1
        else:
            self.high_pressure_counter = 0

        if self.is_high_pressure():
            self.send_alert()
            self.high_pressure_counter = 0


    def start_consuming(self):
        def callback(ch, method, properties, body):
            measurement = int(body.decode())
            print(f'Received measurement: {measurement}')
            self.process_measurement(measurement)

        self.channel.basic_consume(queue='pressureQueue', on_message_callback=callback, auto_ack=True)
        self.channel.start_consuming()

    def send_alert(self):
        message = 'High pressure alert: 2 consecutive readings above 8 bar detected.'
        self.channel.basic_publish(exchange='', routing_key='pressureAlertQueue', body=message)
        print(f'Alert sent: {message}')


    def is_high_pressure(self):
        return self.high_pressure_counter >= 2


    def close_connection(self):
        self.connection.close()

if __name__ == '__main__':
    consumer = PressureAlertProcessor()
    try:
        consumer.start_consuming()
    except KeyboardInterrupt:
        consumer.close_connection()