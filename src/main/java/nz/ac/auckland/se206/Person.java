package nz.ac.auckland.se206;

import nz.ac.auckland.apiproxy.chat.openai.ChatCompletionRequest;
import nz.ac.auckland.apiproxy.config.ApiProxyConfig;
import nz.ac.auckland.apiproxy.exceptions.ApiProxyException;
import nz.ac.auckland.apiproxy.tts.TextToSpeechRequest.Voice;

public class Person {
  private String name;
  private String role;
  private String profession;
  private String color = "";
  private Voice voice;
  private Boolean talked = false;
  private ChatCompletionRequest chatCompletionRequest;

  public Person(String name, String role, String profession, Voice voice) {
    this.voice = voice;
    this.name = name;
    this.role = role;
    this.profession = profession;
    try{
    ApiProxyConfig config = ApiProxyConfig.readConfig();
      chatCompletionRequest =
          new ChatCompletionRequest(config)
              .setN(1)
              .setTemperature(0.2)
              .setTopP(0.5)
              .setMaxTokens(100);
    } catch (ApiProxyException e) {
      e.printStackTrace();
    }
  }

  public String getName() {
    return name;
  }

  public String getRole() {
    return role;
  }

  public String getProfession() {
    return profession;
  }

  public String getColor() {
    return color;
  }

  public Voice getVoice() {
    return voice;
  }

  public Boolean hasTalked() {
    return talked;
  }

  public void talked() {
    talked = true;
  }

  public ChatCompletionRequest getChatCompletionRequest() {
    return chatCompletionRequest;
  }

  public void setChatCompletionRequest(ChatCompletionRequest chatCompletionRequest) {
    this.chatCompletionRequest = chatCompletionRequest;
  }
}
