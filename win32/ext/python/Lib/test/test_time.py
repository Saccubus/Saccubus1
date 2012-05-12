from test import support
import time
import unittest
import locale
import sysconfig
import sys
import warnings

class TimeTestCase(unittest.TestCase):

    def setUp(self):
        self.t = time.time()

    def test_data_attributes(self):
        time.altzone
        time.daylight
        time.timezone
        time.tzname

    def test_clock(self):
        time.clock()

    def test_conversions(self):
        self.assertEqual(time.ctime(self.t),
                         time.asctime(time.localtime(self.t)))
        self.assertEqual(int(time.mktime(time.localtime(self.t))),
                         int(self.t))

    def test_sleep(self):
        time.sleep(1.2)

    def test_strftime(self):
        tt = time.gmtime(self.t)
        for directive in ('a', 'A', 'b', 'B', 'c', 'd', 'H', 'I',
                          'j', 'm', 'M', 'p', 'S',
                          'U', 'w', 'W', 'x', 'X', 'y', 'Y', 'Z', '%'):
            format = ' %' + directive
            try:
                time.strftime(format, tt)
            except ValueError:
                self.fail('conversion specifier: %r failed.' % format)

        # Issue #10762: Guard against invalid/non-supported format string
        # so that Python don't crash (Windows crashes when the format string
        # input to [w]strftime is not kosher.
        if sys.platform.startswith('win'):
            with self.assertRaises(ValueError):
                time.strftime('%f')

    def _bounds_checking(self, func=time.strftime):
        # Make sure that strftime() checks the bounds of the various parts
        #of the time tuple (0 is valid for *all* values).

        # The year field is tested by other test cases above

        # Check month [1, 12] + zero support
        self.assertRaises(ValueError, func,
                            (1900, -1, 1, 0, 0, 0, 0, 1, -1))
        self.assertRaises(ValueError, func,
                            (1900, 13, 1, 0, 0, 0, 0, 1, -1))
        # Check day of month [1, 31] + zero support
        self.assertRaises(ValueError, func,
                            (1900, 1, -1, 0, 0, 0, 0, 1, -1))
        self.assertRaises(ValueError, func,
                            (1900, 1, 32, 0, 0, 0, 0, 1, -1))
        # Check hour [0, 23]
        self.assertRaises(ValueError, func,
                            (1900, 1, 1, -1, 0, 0, 0, 1, -1))
        self.assertRaises(ValueError, func,
                            (1900, 1, 1, 24, 0, 0, 0, 1, -1))
        # Check minute [0, 59]
        self.assertRaises(ValueError, func,
                            (1900, 1, 1, 0, -1, 0, 0, 1, -1))
        self.assertRaises(ValueError, func,
                            (1900, 1, 1, 0, 60, 0, 0, 1, -1))
        # Check second [0, 61]
        self.assertRaises(ValueError, func,
                            (1900, 1, 1, 0, 0, -1, 0, 1, -1))
        # C99 only requires allowing for one leap second, but Python's docs say
        # allow two leap seconds (0..61)
        self.assertRaises(ValueError, func,
                            (1900, 1, 1, 0, 0, 62, 0, 1, -1))
        # No check for upper-bound day of week;
        #  value forced into range by a ``% 7`` calculation.
        # Start check at -2 since gettmarg() increments value before taking
        #  modulo.
        self.assertRaises(ValueError, func,
                            (1900, 1, 1, 0, 0, 0, -2, 1, -1))
        # Check day of the year [1, 366] + zero support
        self.assertRaises(ValueError, func,
                            (1900, 1, 1, 0, 0, 0, 0, -1, -1))
        self.assertRaises(ValueError, func,
                            (1900, 1, 1, 0, 0, 0, 0, 367, -1))

    def test_strftime_bounding_check(self):
        self._bounds_checking(lambda tup: time.strftime('', tup))

    def test_default_values_for_zero(self):
        # Make sure that using all zeros uses the proper default values.
        # No test for daylight savings since strftime() does not change output
        # based on its value.
        expected = "2000 01 01 00 00 00 1 001"
        with support.check_warnings():
            result = time.strftime("%Y %m %d %H %M %S %w %j", (0,)*9)
        self.assertEqual(expected, result)

    def test_strptime(self):
        # Should be able to go round-trip from strftime to strptime without
        # throwing an exception.
        tt = time.gmtime(self.t)
        for directive in ('a', 'A', 'b', 'B', 'c', 'd', 'H', 'I',
                          'j', 'm', 'M', 'p', 'S',
                          'U', 'w', 'W', 'x', 'X', 'y', 'Y', 'Z', '%'):
            format = '%' + directive
            strf_output = time.strftime(format, tt)
            try:
                time.strptime(strf_output, format)
            except ValueError:
                self.fail("conversion specifier %r failed with '%s' input." %
                          (format, strf_output))

    def test_strptime_bytes(self):
        # Make sure only strings are accepted as arguments to strptime.
        self.assertRaises(TypeError, time.strptime, b'2009', "%Y")
        self.assertRaises(TypeError, time.strptime, '2009', b'%Y')

    def test_asctime(self):
        time.asctime(time.gmtime(self.t))

        # Max year is only limited by the size of C int.
        sizeof_int = sysconfig.get_config_var('SIZEOF_INT') or 4
        bigyear = (1 << 8 * sizeof_int - 1) - 1
        asc = time.asctime((bigyear, 6, 1) + (0,)*6)
        self.assertEqual(asc[-len(str(bigyear)):], str(bigyear))
        self.assertRaises(OverflowError, time.asctime, (bigyear + 1,) + (0,)*8)
        self.assertRaises(TypeError, time.asctime, 0)
        self.assertRaises(TypeError, time.asctime, ())
        self.assertRaises(TypeError, time.asctime, (0,) * 10)

    def test_asctime_bounding_check(self):
        self._bounds_checking(time.asctime)

    def test_ctime(self):
        t = time.mktime((1973, 9, 16, 1, 3, 52, 0, 0, -1))
        self.assertEqual(time.ctime(t), 'Sun Sep 16 01:03:52 1973')
        t = time.mktime((2000, 1, 1, 0, 0, 0, 0, 0, -1))
        self.assertEqual(time.ctime(t), 'Sat Jan  1 00:00:00 2000')
        for year in [-100, 100, 1000, 2000, 10000]:
            try:
                testval = time.mktime((year, 1, 10) + (0,)*6)
            except (ValueError, OverflowError):
                # If mktime fails, ctime will fail too.  This may happen
                # on some platforms.
                pass
            else:
                self.assertEqual(time.ctime(testval)[20:], str(year))

    @unittest.skipIf(not hasattr(time, "tzset"),
        "time module has no attribute tzset")
    def test_tzset(self):

        from os import environ

        # Epoch time of midnight Dec 25th 2002. Never DST in northern
        # hemisphere.
        xmas2002 = 1040774400.0

        # These formats are correct for 2002, and possibly future years
        # This format is the 'standard' as documented at:
        # http://www.opengroup.org/onlinepubs/007904975/basedefs/xbd_chap08.html
        # They are also documented in the tzset(3) man page on most Unix
        # systems.
        eastern = 'EST+05EDT,M4.1.0,M10.5.0'
        victoria = 'AEST-10AEDT-11,M10.5.0,M3.5.0'
        utc='UTC+0'

        org_TZ = environ.get('TZ',None)
        try:
            # Make sure we can switch to UTC time and results are correct
            # Note that unknown timezones default to UTC.
            # Note that altzone is undefined in UTC, as there is no DST
            environ['TZ'] = eastern
            time.tzset()
            environ['TZ'] = utc
            time.tzset()
            self.assertEqual(
                time.gmtime(xmas2002), time.localtime(xmas2002)
                )
            self.assertEqual(time.daylight, 0)
            self.assertEqual(time.timezone, 0)
            self.assertEqual(time.localtime(xmas2002).tm_isdst, 0)

            # Make sure we can switch to US/Eastern
            environ['TZ'] = eastern
            time.tzset()
            self.assertNotEqual(time.gmtime(xmas2002), time.localtime(xmas2002))
            self.assertEqual(time.tzname, ('EST', 'EDT'))
            self.assertEqual(len(time.tzname), 2)
            self.assertEqual(time.daylight, 1)
            self.assertEqual(time.timezone, 18000)
            self.assertEqual(time.altzone, 14400)
            self.assertEqual(time.localtime(xmas2002).tm_isdst, 0)
            self.assertEqual(len(time.tzname), 2)

            # Now go to the southern hemisphere.
            environ['TZ'] = victoria
            time.tzset()
            self.assertNotEqual(time.gmtime(xmas2002), time.localtime(xmas2002))
            self.assertTrue(time.tzname[0] == 'AEST', str(time.tzname[0]))
            self.assertTrue(time.tzname[1] == 'AEDT', str(time.tzname[1]))
            self.assertEqual(len(time.tzname), 2)
            self.assertEqual(time.daylight, 1)
            self.assertEqual(time.timezone, -36000)
            self.assertEqual(time.altzone, -39600)
            self.assertEqual(time.localtime(xmas2002).tm_isdst, 1)

        finally:
            # Repair TZ environment variable in case any other tests
            # rely on it.
            if org_TZ is not None:
                environ['TZ'] = org_TZ
            elif 'TZ' in environ:
                del environ['TZ']
            time.tzset()

    def test_insane_timestamps(self):
        # It's possible that some platform maps time_t to double,
        # and that this test will fail there.  This test should
        # exempt such platforms (provided they return reasonable
        # results!).
        for func in time.ctime, time.gmtime, time.localtime:
            for unreasonable in -1e200, 1e200:
                self.assertRaises(ValueError, func, unreasonable)

    def test_ctime_without_arg(self):
        # Not sure how to check the values, since the clock could tick
        # at any time.  Make sure these are at least accepted and
        # don't raise errors.
        time.ctime()
        time.ctime(None)

    def test_gmtime_without_arg(self):
        gt0 = time.gmtime()
        gt1 = time.gmtime(None)
        t0 = time.mktime(gt0)
        t1 = time.mktime(gt1)
        self.assertAlmostEqual(t1, t0, delta=0.2)

    def test_localtime_without_arg(self):
        lt0 = time.localtime()
        lt1 = time.localtime(None)
        t0 = time.mktime(lt0)
        t1 = time.mktime(lt1)
        self.assertAlmostEqual(t1, t0, delta=0.2)

class TestLocale(unittest.TestCase):
    def setUp(self):
        self.oldloc = locale.setlocale(locale.LC_ALL)

    def tearDown(self):
        locale.setlocale(locale.LC_ALL, self.oldloc)

    def test_bug_3061(self):
        try:
            tmp = locale.setlocale(locale.LC_ALL, "fr_FR")
        except locale.Error:
            # skip this test
            return
        # This should not cause an exception
        time.strftime("%B", (2009,2,1,0,0,0,0,0,0))


class _BaseYearTest(unittest.TestCase):
    accept2dyear = None

    def setUp(self):
        self.saved_accept2dyear = time.accept2dyear
        time.accept2dyear = self.accept2dyear

    def tearDown(self):
        time.accept2dyear = self.saved_accept2dyear

    def yearstr(self, y):
        raise NotImplementedError()

class _TestAsctimeYear:
    def yearstr(self, y):
        return time.asctime((y,) + (0,) * 8).split()[-1]

    def test_large_year(self):
        # Check that it doesn't crash for year > 9999
        self.assertEqual(self.yearstr(12345), '12345')
        self.assertEqual(self.yearstr(123456789), '123456789')

class _TestStrftimeYear:
    def yearstr(self, y):
        return time.strftime('%Y', (y,) + (0,) * 8).split()[-1]

    def test_large_year(self):
        # Check that it doesn't crash for year > 9999
        try:
            text = self.yearstr(12345)
        except ValueError:
            # strftime() is limited to [1; 9999] with Visual Studio
            return
        self.assertEqual(text, '12345')
        self.assertEqual(self.yearstr(123456789), '123456789')

class _Test2dYear(_BaseYearTest):
    accept2dyear = 1

    def test_year(self):
        with support.check_warnings():
            self.assertEqual(self.yearstr(0), '2000')
            self.assertEqual(self.yearstr(69), '1969')
            self.assertEqual(self.yearstr(68), '2068')
            self.assertEqual(self.yearstr(99), '1999')

    def test_invalid(self):
        self.assertRaises(ValueError, self.yearstr, -1)
        self.assertRaises(ValueError, self.yearstr, 100)
        self.assertRaises(ValueError, self.yearstr, 999)

class _Test4dYear(_BaseYearTest):
    accept2dyear = 0

    def test_year(self):
        self.assertIn(self.yearstr(1),     ('1', '0001'))
        self.assertIn(self.yearstr(68),   ('68', '0068'))
        self.assertIn(self.yearstr(69),   ('69', '0069'))
        self.assertIn(self.yearstr(99),   ('99', '0099'))
        self.assertIn(self.yearstr(999), ('999', '0999'))
        self.assertEqual(self.yearstr(9999), '9999')

    def test_negative(self):
        try:
            text = self.yearstr(-1)
        except ValueError:
            # strftime() is limited to [1; 9999] with Visual Studio
            return
        self.assertIn(text, ('-1', '-001'))

        self.assertEqual(self.yearstr(-1234), '-1234')
        self.assertEqual(self.yearstr(-123456), '-123456')


    def test_mktime(self):
        # Issue #1726687
        for t in (-2, -1, 0, 1):
            try:
                tt = time.localtime(t)
            except (OverflowError, ValueError):
                pass
            else:
                self.assertEqual(time.mktime(tt), t)
        # It may not be possible to reliably make mktime return error
        # on all platfom.  This will make sure that no other exception
        # than OverflowError is raised for an extreme value.
        try:
            time.mktime((-1, 1, 1, 0, 0, 0, -1, -1, -1))
        except OverflowError:
            pass

class TestAsctimeAccept2dYear(_TestAsctimeYear, _Test2dYear):
    pass

class TestStrftimeAccept2dYear(_TestStrftimeYear, _Test2dYear):
    pass

class TestAsctime4dyear(_TestAsctimeYear, _Test4dYear):
    pass

class TestStrftime4dyear(_TestStrftimeYear, _Test4dYear):
    pass

class Test2dyearBool(_TestAsctimeYear, _Test2dYear):
    accept2dyear = True

class Test4dyearBool(_TestAsctimeYear, _Test4dYear):
    accept2dyear = False

class TestAccept2YearBad(_TestAsctimeYear, _BaseYearTest):
    class X:
        def __bool__(self):
            raise RuntimeError('boo')
    accept2dyear = X()
    def test_2dyear(self):
        pass
    def test_invalid(self):
        self.assertRaises(RuntimeError, self.yearstr, 200)


def test_main():
    support.run_unittest(
        TimeTestCase,
        TestLocale,
        TestAsctimeAccept2dYear,
        TestStrftimeAccept2dYear,
        TestAsctime4dyear,
        TestStrftime4dyear,
        Test2dyearBool,
        Test4dyearBool,
        TestAccept2YearBad)

if __name__ == "__main__":
    test_main()
