import pika

class AlertReportingClient():

    connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
    channel = connection.channel()

    channel.queue_declare(queue='pressureAlertQueue')

    def callback(ch, method, properties, body):
        message = body.decode()
        print(f'{message}')
        ch.basic_ack(delivery_tag = method.delivery_tag)

    channel.basic_consume(queue='pressureAlertQueue', on_message_callback=callback)

    print('Waiting for messages')
    channel.start_consuming()