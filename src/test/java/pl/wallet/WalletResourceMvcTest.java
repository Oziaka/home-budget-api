package pl.wallet;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.exception.ErrorsResponse;
import pl.test_tool.RandomTool;
import pl.test_tool.UserTool;
import pl.test_tool.error.HibernateErrorTool;
import pl.test_tool.error.WalletError;
import pl.test_tool.web.RequestToolWithAuth;
import pl.user.UserDto;

import java.util.Collections;

import static pl.test_tool.error.ServerError.IS_NOT_YOU_PROPERTY;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class WalletResourceMvcTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void should_not_remove_wallet_another_user () {
    should_add_wallet();
    UserDto userDto = pl.user.UserTool.register(mockMvc);
    ErrorsResponse errorsResponse = HibernateErrorTool.buildErrorResponse(IS_NOT_YOU_PROPERTY);
    //when

    //then
    RequestToolWithAuth.checkResponseDelete(mockMvc, "/wallet/remove/1", HttpStatus.INTERNAL_SERVER_ERROR, errorsResponse.getErrors(), userDto.getEmail());
  }

  @Test
  void should_remove_wallet () {
    UserDto userDto = pl.user.UserTool.register(mockMvc);
    WalletDto walletDto = WalletDto.builder().name(RandomTool.getRandomString()).balance(RandomTool.id()).build();
    WalletDto expectedWalletDto = WalletDto.builder()
      .id(1L)
      .name(walletDto.getName())
      .balance(walletDto.getBalance())
      .users(Collections.singletonList(UserDto.builder().email(userDto.getEmail()).build()))
      .build();
    addWallet(walletDto, HttpStatus.CREATED, expectedWalletDto, userDto.getEmail());
    //when

    //then
    RequestToolWithAuth.checkResponseDelete(mockMvc, "/wallet/remove/1", HttpStatus.NO_CONTENT, userDto.getEmail());

  }

  @Test
  void should_add_wallet () {
    //given
    UserDto userDto = pl.user.UserTool.register(mockMvc);
    WalletDto walletDto = WalletDto.builder().name(RandomTool.getRandomString()).balance(RandomTool.id()).build();
    WalletDto expectedWalletDto = WalletDto.builder()
      .id(1L)
      .name(walletDto.getName())
      .balance(walletDto.getBalance())
      .users(Collections.singletonList(UserDto.builder().email(userDto.getEmail()).build()))
      .build();
    //when

    //then
    System.out.println("------");
    System.out.println(expectedWalletDto.toString());
    System.out.println("------");
    addWallet(walletDto, HttpStatus.CREATED, expectedWalletDto, userDto.getEmail());
  }

  @Test
  void wallet_should_have_balance () {
    //given
    UserDto userDto = UserTool.register(mockMvc);
    WalletDto walletDto = WalletDto.builder().name(RandomTool.getRandomString()).build();
    ErrorsResponse errorsResponse = HibernateErrorTool.buildErrorResponse(WalletError.BALANCE_NOT_NULL);
    //when

    //then
    addWallet(walletDto, HttpStatus.BAD_REQUEST, errorsResponse, userDto.getEmail());
  }

  @Test
  void wallet_should_have_name () {
    //given
    UserDto userDto = UserTool.register(mockMvc);
    WalletDto walletDto = WalletDto.builder().balance(RandomTool.id()).build();
    ErrorsResponse errorsResponse = HibernateErrorTool.buildErrorResponse(WalletError.NAME_NOT_NULL);
    //when

    //then
    addWallet(walletDto, HttpStatus.BAD_REQUEST, errorsResponse, userDto.getEmail());
  }

  @Test
  void new_wallet_should_not_have_id () {
    //given
    UserDto userDto = UserTool.register(mockMvc);
    WalletDto walletDto = WalletDto.builder()
      .id(RandomTool.id())
      .name(RandomTool.getRandomString())
      .balance(RandomTool.id())
      .build();
    ErrorsResponse errorsResponse = HibernateErrorTool.buildErrorResponse(WalletError.ID_NULL);
    //when

    //then
    System.out.println(errorsResponse.getErrors().toString());
    addWallet(walletDto, HttpStatus.BAD_REQUEST, errorsResponse, userDto.getEmail());
  }


  void addWallet (Object object, HttpStatus expectHttpStatus, Object resultObject, String userName) {
    RequestToolWithAuth.checkResponse(HttpMethod.POST, mockMvc, "/wallet/add", object, expectHttpStatus, resultObject, userName);
  }


}