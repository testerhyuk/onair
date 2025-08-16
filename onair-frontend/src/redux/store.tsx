import { configureStore } from "@reduxjs/toolkit";
import { persistStore, persistReducer } from "redux-persist";
import storage from "redux-persist/lib/storage"; // localStorage 사용
import authReducer from "./authSlice";

// persist 설정
const persistConfig = {
  key: "auth",
  storage,
  whitelist: ["memberId", "nickname", "accessToken", "role"], // 유지할 state
};

const persistedReducer = persistReducer(persistConfig, authReducer);

export const store = configureStore({
  reducer: {
    auth: persistedReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: {
        ignoredActions: [
          "persist/PERSIST",
          "persist/REHYDRATE",
          "persist/PAUSE",
          "persist/FLUSH",
          "persist/PURGE",
          "persist/REGISTER",
        ],
      },
    }),
});

export const persistor = persistStore(store);

export type RootState = ReturnType<typeof store.getState>;
export type AppDispatch = typeof store.dispatch;
