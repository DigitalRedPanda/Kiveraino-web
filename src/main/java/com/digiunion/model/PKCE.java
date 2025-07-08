package com.digiunion.model;

public record PKCE(String verifier, String challenge){
  
  public boolean isEmpty() {
    return verifier == null;
  }

  @Override
  public String toString() {
    return new StringBuilder(verifier).append(' ').append(challenge).toString();
  };
}
