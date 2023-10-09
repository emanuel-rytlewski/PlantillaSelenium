package utils;

import com.fasterxml.jackson.annotation.JsonProperty;


public final class TestAccount {
  @JsonProperty ("email")
  private String email = "";

  @JsonProperty ("password")
  private String password = "";

  @JsonProperty ("firstName")
  private String firstName = "";

  @JsonProperty ("lastName")
  private String lastName = "";

  @JsonProperty("walletId")
  private String walletId = "";


  /**
   * Default constructor.
   */
  public TestAccount() {

  }

  /**
   * Constructor of the TestAccount class. Test Account include the following information
   * @param email user's email
   * @param password user's password
   * @param firstName user's firstname
   * @param lastName user's lastname
   * @param walletId user's walletID
   */
  public TestAccount(String email, String password, String firstName, String lastName,
                     String walletId) {
    this.email = email;
    this.password = password;
    this.firstName = firstName;
    this.lastName = lastName;
    this.walletId = walletId;
  }

  public String email() {
    return this.email;
  }

  public String password() {
    return this.password;
  }

  public String firstName() {
    return this.firstName;
  }

  public String lastName() {
    return this.lastName;
  }

  public String walletId() {
    return this.walletId;
  }
}
