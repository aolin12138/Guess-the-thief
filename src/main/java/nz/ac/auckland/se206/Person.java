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
   * @param name the name of the person
   * @param role the role of the person
   * @param profession the profession of the person
   * @param voice the voice of the person
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
   * Gets the name of the person.
   *
   * @return the name of the person
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the role of the person.
   *
   * @return the role of the person
   */
  public String getRole() {
    return role;
  }

  /**
   * Gets the profession of the person.
   *
   * @return the profession of the person
   */
  public String getProfession() {
    return profession;
  }

  /**
   * Gets the color associated with the person.
   *
   * @return the color of the person
   */
  public String getColor() {
    return color;
  }

  /**
   * Gets the voice associated with the person.
   *
   * @return the voice of the person
   */
  public Voice getVoice() {
    return voice;
  }

  /**
   * Checks if the person has talked.
   *
   * @return true if the person has talked, false otherwise
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
   * Checks if the person has been interviewed.
   *
   * @return true if the person has been interviewed, false otherwise
   */
  public boolean hasBeenInterviewed() {
    return isInterviewed;
  }

  /**
   * Gets the chat completion request.
   *
   * @return the chat completion request
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
