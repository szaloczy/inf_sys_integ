import random
import unittest
from unittest.mock import patch, MagicMock
from pressure_alert_processor import PressureAlertProcessor

@patch("pressure_alert_processor.pika.BlockingConnection")
class TestPressureAlertProcessor(unittest.TestCase):


    def test_pressure_is_not_high(self, mock_connection):
        mock_connection.return_value = MagicMock()
        processor = PressureAlertProcessor()
        processor.high_pressure_counter = 1

        self.assertFalse(processor.is_high_pressure())


    def test_pressure_is_high(self, mock_connection):
        mock_connection.return_value = MagicMock()
        processor = PressureAlertProcessor()
        processor.high_pressure_counter = 3

        self.assertTrue(processor.is_high_pressure())


    def test_counter_increase_above_8(self):
        pass


    def test_counter_resets_below_8(self):
        pass


if __name__ == '__main__':
    unittest.main()