import unittest

from unittest.mock import patch, MagicMock
from pressure_alert_processor import PressureAlertProcessor

class TestPressureAlertProcessor(unittest.TestCase):

    def setUp(self):
        patcher = patch('pressure_alert_processor.pika.BlockingConnection')
        self.mock_blocking_conn = patcher.start()
        self.addCleanup(patcher.stop)

        mock_connection = MagicMock()
        mock_channel = MagicMock()
        mock_connection.channel.return_value = mock_channel
        self.mock_blocking_conn.return_value = mock_connection


    def test_is_high_pressure_func_returns_false(self):
        processor = PressureAlertProcessor()
        processor.high_pressure_counter = 1
        self.assertFalse(processor.is_high_pressure())


    def test_is_high_pressure_func_returns_true(self):
        processor = PressureAlertProcessor()
        processor.high_pressure_counter = 3
        self.assertTrue(processor.is_high_pressure())


    def test_counter_increase_above_8(self):
        processor = PressureAlertProcessor()

        processor.process_measurement(9)
        self.assertEqual(processor.high_pressure_counter, 1)


    def test_counter_not_increase_below_or_equal_8(self):
        processor = PressureAlertProcessor()

        processor.process_measurement(7)
        processor.process_measurement(8)

        self.assertEqual(processor.high_pressure_counter, 0)


    def test_alert_after_two_consecutive_high(self):
        processor = PressureAlertProcessor()
        processor.send_alert = MagicMock()

        processor.process_measurement(9)
        processor.process_measurement(9)

        processor.send_alert.assert_called_once()
        self.assertEqual(processor.high_pressure_counter, 0)


    def test_four_high_readings_trigger_two_alerts(self):
        processor = PressureAlertProcessor()
        processor.send_alert = MagicMock()

        processor.process_measurement(9)
        processor.process_measurement(9)
        processor.process_measurement(9)
        processor.process_measurement(9)

        self.assertEqual(processor.send_alert.call_count, 2)


    def test_no_alert_if_not_consecutive(self):
        processor = PressureAlertProcessor()
        processor.send_alert = MagicMock()

        processor.process_measurement(9)
        processor.process_measurement(7)
        processor.process_measurement(9)

        processor.send_alert.assert_not_called()
        self.assertTrue(processor.high_pressure_counter, 1)

if __name__ == '__main__':
    unittest.main()