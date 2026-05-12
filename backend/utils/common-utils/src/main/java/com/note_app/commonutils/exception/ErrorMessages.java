package com.note_app.commonutils.exception;

public final class ErrorMessages {

    private ErrorMessages() {
    }

    public static final String AUTH_HEADER_MISSING = "Kullanici basligi (X-User-Id) eksik";
    public static final String AUTH_ROLE_MISSING = "Kullanici rolu (X-User-Role) eksik";
    public static final String AUTH_TOKEN_INVALID = "Token gecersiz veya suresi dolmus";
    public static final String AUTH_ROLE_DENIED = "Bu islem icin yetkiniz yok";
    public static final String AUTH_INVALID_CREDENTIALS = "E-posta veya sifre hatali";

    public static final String USER_NOT_FOUND = "Kullanici bulunamadi";
    public static final String USER_EMAIL_BLANK = "E-posta bos olamaz";
    public static final String USER_PASSWORD_SHORT = "Sifre en az 6 karakter olmalidir";
    public static final String USER_EMAIL_EXISTS = "Bu e-posta zaten kayitli";
    public static final String USER_FORBIDDEN_SELF_ONLY = "Sadece kendi hesabiniz uzerinde islem yapabilirsiniz";

    public static final String NOTE_NOT_FOUND = "Not bulunamadi";
    public static final String NOTE_TITLE_BLANK = "Notun basligi bos olamaz";
    public static final String NOTE_TITLE_TOO_LONG = "Baslik en fazla 255 karakter olabilir";
    public static final String NOTE_FORBIDDEN_OTHER_USER = "Baska bir kullanicinin notuna erisemezsiniz";

    public static final String CATEGORY_NOT_FOUND = "Kategori bulunamadi";
    public static final String CATEGORY_NAME_BLANK = "Kategori adi bos olamaz";
    public static final String CATEGORY_NAME_TOO_LONG = "Kategori adi en fazla 100 karakter olabilir";
    public static final String CATEGORY_NAME_EXISTS = "Bu isimde bir kategoriniz zaten var";
    public static final String CATEGORY_FORBIDDEN_OTHER_USER = "Baska bir kullanicinin kategorisine erisemezsiniz";

    public static final String FILE_NOT_FOUND = "Dosya bulunamadi";
    public static final String FILE_EMPTY = "Dosya bos olamaz";
    public static final String FILE_ID_INVALID = "Gecersiz dosya kimligi";
    public static final String FILE_FORBIDDEN_OTHER_USER = "Bu dosyaya erisme yetkiniz yok";
    public static final String FILE_UPLOAD_FAILED = "Dosya yuklenirken hata olustu";
    public static final String FILE_DOWNLOAD_FAILED = "Dosya indirilirken hata olustu";

    public static final String SEARCH_KEYWORD_BLANK = "Arama kelimesi bos olamaz";
    public static final String SEARCH_TYPE_UNKNOWN = "Bilinmeyen arama tipi";

    public static final String VALIDATION_FAILED = "Dogrulama hatasi";
    public static final String GATEWAY_NO_ROUTE = "Yonlendirme tanimi bulunamadi";
    public static final String GATEWAY_TARGET_DOWN = "Hedef servise ulasilamiyor";

    public static String withId(String base, Object id) {
        return base + ": " + id;
    }
}
