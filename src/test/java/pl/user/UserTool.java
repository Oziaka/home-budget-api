package pl.user;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import pl.test_tool.RandomTool;
import pl.test_tool.web.RequestTool;


public class UserTool {

  private static String[] emailDomainName = new String[]{"o2.pl", "gmail.com", "wp.pl", "onet.pl"};

  private static String randomCorrectEmail () {
    return RandomTool.getRandomString(10) + "@" + randomEmailDomainName();
  }

  private static String randomEmailDomainName () {
    return UserTool.emailDomainName[RandomTool.getNumberInteger() % UserTool.emailDomainName.length];
  }

  private static UserDto createRandomUserDto () {
    return UserDto.builder()
      .email(randomCorrectEmail())
      .password(RandomTool.getRandomString())
      .build();
  }

  public static UserDto register (MockMvc mockMvc) {
    UserDto userDto = UserTool.createRandomUserDto();
    UserDto userDtoExpectedResult = UserDto.builder().email(userDto.getEmail()).build();
    RequestTool.checkResponse(HttpMethod.POST, mockMvc, "/register", userDto, HttpStatus.CREATED, userDtoExpectedResult);
    return userDto;
  }

  @Deprecated
  public static UserDto registerRandomUser (UserController userController) {
    UserDto userDto = UserTool.createRandomUserDto();
    return userController.addWithDefaultRoleAndDefaultCategory(userDto);
  }

  public static UserDto registerRandomUser (RegisterResource registerResource) {
    UserDto userDto = UserTool.createRandomUserDto();
    return registerResource.register(userDto).getBody();
  }
}