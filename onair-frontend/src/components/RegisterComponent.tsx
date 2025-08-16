import React, { useState } from "react";
import { signup, type SignUpRequest } from "../api/memberApi";
import { useNavigate } from "react-router";

declare global {
  interface Window {
    daum: any; // 카카오 주소 검색 API
  }
}

export default function RegisterComponent() {
  const navigate = useNavigate();

  const [form, setForm] = useState<SignUpRequest>({
    email: "",
    password: "",
    checkPassword: "",
    nickname: "",
    zipCode: "",
    address: "",
    detailAddress: "",
    role: "USER", // 기본값
  });

  const [loading, setLoading] = useState(false);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleAddressSearch = () => {
    new window.daum.Postcode({
      oncomplete: (data: any) => {
        setForm((prev) => ({
          ...prev,
          zipCode: data.zonecode,
          address: data.address,
        }));
      },
    }).open();
  };

  const handleRoleSelect = (role: "USER" | "REPORTER") => {
    setForm((prev) => ({ ...prev, role }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (form.password !== form.checkPassword) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    if (!form.address) {
      alert("주소를 선택해주세요.");
      return;
    }

    setLoading(true);

    try {
      await signup(form);
      alert("회원가입이 완료되었습니다.");
      navigate("/"); // 성공 시 메인페이지 이동
    } catch (err: any) {
      console.error(err);
      alert("회원가입에 실패했습니다."); // 실패 시 알림
    } finally {
      setLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="w-full max-w-lg p-6 rounded shadow">
      <h2 className="text-xl font-semibold mb-4">회원가입</h2>

      {/* 이메일 */}
      <div className="mb-3">
        <label className="block mb-1">이메일</label>
        <input
          type="email"
          name="email"
          value={form.email}
          onChange={handleChange}
          className="w-full border px-3 py-1.5 rounded"
          required
        />
      </div>

      {/* 비밀번호 */}
      <div className="mb-3">
        <label className="block mb-1">비밀번호</label>
        <input
          type="password"
          name="password"
          value={form.password}
          onChange={handleChange}
          className="w-full border px-3 py-1.5 rounded"
          required
        />
      </div>

      {/* 비밀번호 확인 */}
      <div className="mb-3">
        <label className="block mb-1">비밀번호 확인</label>
        <input
          type="password"
          name="checkPassword"
          value={form.checkPassword}
          onChange={handleChange}
          className="w-full border px-3 py-1.5 rounded"
          required
        />
      </div>

      {/* 닉네임 */}
      <div className="mb-3">
        <label className="block mb-1">닉네임</label>
        <input
          type="text"
          name="nickname"
          value={form.nickname}
          onChange={handleChange}
          className="w-full border px-3 py-1.5 rounded"
          required
        />
      </div>

      {/* 역할 선택 */}
      <div className="mb-4">
        <label className="block mb-1">회원 유형</label>
        <div className="flex space-x-2">
          <button
            type="button"
            onClick={() => handleRoleSelect("USER")}
            className={`flex-1 py-2 rounded border cursor-pointer ${
              form.role === "USER" ? "bg-blue-500 text-white" : "bg-gray-200"
            }`}
          >
            일반
          </button>
          <button
            type="button"
            onClick={() => handleRoleSelect("REPORTER")}
            className={`flex-1 py-2 rounded border cursor-pointer ${
              form.role === "REPORTER" ? "bg-blue-500 text-white" : "bg-gray-200"
            }`}
          >
            기자
          </button>
        </div>
      </div>

      {/* 우편번호 / 주소 */}
      <div className="mb-3">
        <label className="block mb-1">우편번호 / 주소</label>
        <div className="flex space-x-2">
          <input
            type="text"
            name="zipCode"
            value={form.zipCode}
            readOnly
            className="flex-1 border px-3 py-1.5 rounded"
            placeholder="우편번호"
          />
          <button
            type="button"
            onClick={handleAddressSearch}
            className="bg-gray-200 px-4 rounded hover:bg-gray-300 cursor-pointer"
          >
            주소 검색
          </button>
        </div>
        <input
          type="text"
          name="address"
          value={form.address}
          readOnly
          className="w-full border px-3 py-1.5 rounded mt-2"
          placeholder="주소"
          required
        />
      </div>

      {/* 상세주소 */}
      <div className="mb-3">
        <label className="block mb-1">상세주소</label>
        <input
          type="text"
          name="detailAddress"
          value={form.detailAddress}
          onChange={handleChange}
          className="w-full border px-3 py-1.5 rounded"
        />
      </div>

      {/* 회원가입 버튼 */}
      <button
        type="submit"
        disabled={loading}
        className="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600 cursor-pointer"
      >
        {loading ? "회원가입 중..." : "회원가입"}
      </button>
    </form>
  );
}
