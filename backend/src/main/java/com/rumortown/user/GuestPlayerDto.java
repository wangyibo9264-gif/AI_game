package com.rumortown.user;

public record GuestPlayerDto(Long id, String displayName, boolean guest) {
    public static GuestPlayerDto from(User user) {
        return new GuestPlayerDto(user.getId(), user.getDisplayName(), user.isGuest());
    }
}
