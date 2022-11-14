Cameron Humphreys 101162528


Any test under Log4J tests is an integration/acceptance test that needs to be a review of log4j.log,
the test code will by systematic instructions on the order of running and the expected result.

The main function of these types of tests is to maintain TDD while adding features that cannot be tested in JUnit ie: main methods for playability

currently, either Tester or AllAcceptance tests.feature can be used to run every acceptance test for the game.
