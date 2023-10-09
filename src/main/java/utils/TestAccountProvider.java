package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class TestAccountProvider {
  private static List<TestAccount> testAccountsList = new ArrayList<>();
  private static final SecureRandom random = new SecureRandom();
  private static final Logger logger = LogManager.getLogger(TestAccountProvider.class);

  private TestAccountProvider() {
  }

  static {
    try {
      // create object mapper instance
      ObjectMapper mapper = new ObjectMapper();

      // convert JSON array to list of accounts
      testAccountsList = Arrays
              .asList(mapper.readValue(Paths.get("test-data/testAccounts.json").toFile(),
                      TestAccount[].class));
    } catch (Exception e) {
      logger.error(e);
    }
  }

  /**
   * Get a random TestAccount from the testAccountsList.
   * @return TestAccount object.
   */
  public static TestAccount getAccount() {
    logger.info("Selecting a random account from the list.");
    TestAccount account = testAccountsList.get(random.nextInt(testAccountsList.size()));
    logger.debug("Test account email: {}", account.email());
    logger.debug("Test account password: {}", account.password());
    logger.debug("Test account first name: {}", account.firstName());
    logger.debug("Test account last name: {}", account.lastName());
    return account;
  }
}
