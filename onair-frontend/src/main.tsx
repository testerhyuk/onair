import { createRoot } from "react-dom/client";
import { Provider } from "react-redux";
import { persistor, store } from "./redux/store";
import router from "./router/router";
import { RouterProvider } from "react-router";
import './index.css'
import { PersistGate } from "redux-persist/integration/react";

createRoot(document.getElementById("root")!).render(
  <Provider store={store}>
    <PersistGate loading={null} persistor={persistor}>
      <RouterProvider router={router} />
    </PersistGate>
  </Provider>
);
