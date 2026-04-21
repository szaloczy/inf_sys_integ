import unittest
from unittest.mock import patch, MagicMock

from pressure_generation_client import PressureGenerationClient


class TestPressureGenerationClient(unittest.TestCase):

    def setUp(self):
        patcher = patch('pressure_generation_client.pika.BlockingConnection')
        self.mock_blocking_conn = patcher.start()
        self.addCleanup(patcher.stop)

        self.mock_connection = MagicMock()
        self.mock_channel = MagicMock()
        self.mock_connection.channel.return_value = self.mock_channel
        self.mock_blocking_conn.return_value = self.mock_connection


    def test_init_declares_pressure_queue(self):
        PressureGenerationClient()

        self.mock_channel.queue_declare.assert_called_with(queue='pressureQueue')


    def test_publish_uses_pressure_queue_and_body(self):
        client = PressureGenerationClient()

        measurement = 9
        client.channel.basic_publish(
            exchange='',
            routing_key='pressureQueue',
            body=str(measurement).encode()
        )

        self.mock_channel.basic_publish.assert_called_with(
            exchange='',
            routing_key='pressureQueue',
            body=b'9'
        )


    def test_close_connection(self):
        client = PressureGenerationClient()

        client.close_connection()

        self.mock_connection.close.assert_called_once()


if __name__ == "__main__":
    unittest.main()