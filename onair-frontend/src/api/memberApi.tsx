import axios from "axios";

const API_SERVER_HOST = "http://localhost:9032";

const memberPrefix = `${API_SERVER_HOST}/v1/member`;

export interface SignUpRequest {
  email: string;
  password: string;
  checkPassword: string;
  nickname: string;
  zipCode: string;
  address: string;
  detailAddress: string;
  role: string
}

export interface SignUpResponse {
  memberId: string;
  email: string;
  nickname: string;
  zipCode: string;
  address: string;
  detailAddress: string;
  role: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  memberId: string;
  email: string;
  nickname: string;
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  role: "USER" | "REPORTER"
}

export const withdrawMember = async () => {
  const token = localStorage.getItem("accessToken");
  const res = await axios.delete(`${memberPrefix}/withdraw`, {
    headers: {
      Authorization: `Bearer ${token}`
    },
    withCredentials: true,
  });

  return res.data
}

export const getMemberById = async (memberId: string) => {
  const token = localStorage.getItem("accessToken");
  const res = await axios.get(`${memberPrefix}/info/${memberId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
  return res.data;
};

export const updateMember = async (memberId: string, data: any) => {
  const token = localStorage.getItem("accessToken");
  const res = await axios.put(`${memberPrefix}/modify/${memberId}`, data, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
    withCredentials: true,
  });
  return res.data;
};

export const fetchNickname = async (memberId: string): Promise<string> => {
  try {
    const res = await axios.get(`${memberPrefix}/nickname/${memberId}`);
    
    return res.data;
  } catch (err) {
    console.error(`닉네임 조회 실패: ${memberId}`, err);
    return "알 수 없음";
  }
};

export const signup = async (data: SignUpRequest) => {
  try {
    const response = await axios.post<SignUpResponse>(
      `${memberPrefix}/signup`,
      data
    );
    return response.data;
  } catch (err: any) {
    throw err.response?.data?.message || "회원가입 실패";
  }
};

// 로그인 요청
export const login = async (data: LoginRequest): Promise<LoginResponse> => {
  const res = await axios.post(`${memberPrefix}/login`, data, { withCredentials: true });
  localStorage.setItem("accessToken", res.data.accessToken);
  localStorage.setItem("memberId", String(res.data.memberId));
  localStorage.setItem("nickname", String(res.data.nickname));
  return res.data;
};

// 토큰 재발급 요청
export interface ReissueRequest {
  memberId: string;
  refreshToken: string;
}

export interface ReissueResponse {
  accessToken: string;
}

export const reissueToken = async () => {
  const memberId = localStorage.getItem("memberId");

  return axios.post(`${memberPrefix}/reissue`, {memberId}, { withCredentials: true });
};