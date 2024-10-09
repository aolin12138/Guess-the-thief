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
  private boolean talked = false;
  private boolean isInterviewed = false;

  private ChatCompletionRequest chatCompletionRequest;

  /**
   * Constructor for the Person class
   *
   * @param name
   * @param role
   * @param profession
   * @param voice
   */
  public Person(String name, String role, String profession, Voice voice) {
    this.voice = voice;
    this.name = name;
    this.role = role;
    this.profession = profession;
    try {
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

  /**
   * get the name of the person.
   *
   * @return
   */
  public String getName() {
    return name;
  }

  /**
   * get the role of the person.
   *
   * @return
   */
  public String getRole() {
    return role;
  }

  /**
   * get the profession of the person.
   *
   * @return
   */
  public String getProfession() {
    return profession;
  }

  /**
   * get the color of the person.
   *
   * @return
   */
  public String getColor() {
    return color;
  }

  /**
   * get the voice of the person.
   *
   * @return
   */
  public Voice getVoice() {
    return voice;
  }

  /**
   * boolean for checking if the person has talked.
   *
   * @return
   */
  public boolean hasTalked() {
    return talked;
  }

  /** set the boolean talked to true. */
  public void talked() {
    talked = true;
  }

  /** set the boolean of isInterviewed to true. */
  public void setIsInterviewed() {
    isInterviewed = true;
  }

  /**
   * boolean for checking if the person has been interviewed.
   *
   * @return
   */
  public boolean hasBeenInterviewed() {
    return isInterviewed;
  }

  /**
   * get the chat completion request.
   *
   * @return
   */
  public ChatCompletionRequest getChatCompletionRequest() {
    return chatCompletionRequest;
  }

  /**
   * set the chat completion request.
   *
   * @param chatCompletionRequest
   */
  public void setChatCompletionRequest(ChatCompletionRequest chatCompletionRequest) {
    this.chatCompletionRequest = chatCompletionRequest;
  }
}
