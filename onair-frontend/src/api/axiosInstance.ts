import axios from "axios";

const axiosInstance = axios.create({
  baseURL: "http://localhost:9032",
  withCredentials: true,
});

axiosInstance.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) {
    config.headers["Authorization"] = `Bearer ${token}`;
  }
  return config;
});

axiosInstance.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401) {
      // refreshToken으로 accessToken 재발급
      const refreshToken = localStorage.getItem("refreshToken");
      if (!refreshToken) return Promise.reject(error);

      try {
        const res = await axios.post(
          "http://localhost:9032/v1/auth/refresh",
          {},
          { headers: { Authorization: `Bearer ${refreshToken}` }, withCredentials: true }
        );
        localStorage.setItem("accessToken", res.data.accessToken);

        // 기존 요청 재시도
        error.config.headers["Authorization"] = `Bearer ${res.data.accessToken}`;
        return axios(error.config);
      } catch {
        // refreshToken도 실패 시 로그아웃 처리
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        return Promise.reject(error);
      }
    }
    return Promise.reject(error);
  }
);

export default axiosInstance;
