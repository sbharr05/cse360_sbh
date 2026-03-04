package fieldCheckTools;
public record ValidationResult(boolean valid, String code, String message) {
  public static ValidationResult ok() { return new ValidationResult(true, "OK", ""); }
  public static ValidationResult err(String code, String msg) { return new ValidationResult(false, code, msg); }
}
