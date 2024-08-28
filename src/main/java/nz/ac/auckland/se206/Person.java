package nz.ac.auckland.se206;

import java.util.Random;
import nz.ac.auckland.apiproxy.tts.TextToSpeechRequest.Voice;

public class Person {
  private String name;
  private String role;
  private String profession;
  private String color;
  private Voice[] voices = Voice.values();
  private Voice voice;
  private Boolean talked = false;

  public Person(String name, String role, String profession, String color) {
    int randomIndex = new Random().nextInt(voices.length - 26);
    this.voice = voices[randomIndex];
    this.name = name;
    this.role = role;
    this.profession = profession;
    this.color = color;
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
}
