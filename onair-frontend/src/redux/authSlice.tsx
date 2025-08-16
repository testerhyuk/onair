import { createSlice, type PayloadAction } from "@reduxjs/toolkit";

interface AuthState {
  memberId: string | null;
  nickname: string | null;
  accessToken: string | null;
  role: "USER" | "REPORTER" | null;
}

const initialState: AuthState = {
  memberId: null,
  nickname: null,
  accessToken: null,
  role: null
};

export const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    login: (state, action: PayloadAction<{ memberId: string; nickname: string; accessToken: string; role: "USER" | "REPORTER" }>) => {
      state.memberId = action.payload.memberId;
      state.nickname = action.payload.nickname;
      state.accessToken = action.payload.accessToken;
      state.role = action.payload.role;
    },
    logout: (state) => {
      state.memberId = null;
      state.nickname = null;
      state.accessToken = null;
      state.role = null;
    },
    updateProfile: (state, action: PayloadAction<{ nickname?: string; role?: "USER" | "REPORTER" }>) => {
      if (action.payload.nickname !== undefined) state.nickname = action.payload.nickname;
      if (action.payload.role !== undefined) state.role = action.payload.role;
    },
  },
});

export const { login, logout, updateProfile } = authSlice.actions;
export default authSlice.reducer;
