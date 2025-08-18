import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import { getMemberById, updateMember, withdrawMember } from "../api/memberApi";
import { useDispatch } from "react-redux";
import { logout, updateProfile } from "../redux/authSlice";

interface MemberUpdateForm {
  password?: string;
  checkPassword?: string;
  nickname?: string;
  zipCode?: string;
  address?: string;
  detailAddress?: string;
}

export default function EditProfileComponent() {
  const navigate = useNavigate();
  const memberId = localStorage.getItem("memberId")
  const dispatch = useDispatch();

  const [form, setForm] = useState<MemberUpdateForm>({
    password: "",
    checkPassword: "",
    nickname: "",
    zipCode: "",
    address: "",
    detailAddress: "",
  });

  const [loading, setLoading] = useState(false);

  // 기존 회원 정보 불러오기
  useEffect(() => {
    if (!memberId) return;

    getMemberById(memberId)
      .then((res) => {
        setForm({
          ...form,
          nickname: res.nickname ?? "",
          zipCode: res.zipCode ?? "",
          address: res.address ?? "",
          detailAddress: res.detailAddress ?? "",
        });
      })
      .catch((err) => console.error("회원 정보 불러오기 실패", err));
  }, [memberId]);

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

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (form.password && form.password !== form.checkPassword) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }

    setLoading(true);
    try {
      // 서버에 PUT 요청, null 혹은 빈값은 서버에서 무시됨
      await updateMember(memberId, {
        password: form.password || undefined,
        nickname: form.nickname,
        zipCode: form.zipCode,
        address: form.address,
        detailAddress: form.detailAddress,
      });
      dispatch(updateProfile({nickname: form.nickname}))
      alert("회원 정보가 수정되었습니다.");
      dispatch(logout());
          
      localStorage.removeItem("accessToken");
      localStorage.removeItem("memberId");
      localStorage.removeItem("nickname");

      navigate("/");
    } catch (err) {
      console.error(err);
      alert("수정 실패");
    } finally {
      setLoading(false);
    }
  };

  const handleWithdraw = async () => {
    if (!window.confirm("탈퇴하시면 30일간 재가입이 불가능합니다. 탈퇴하시겠습니까?")) return;

    try {
      await withdrawMember();

      dispatch(logout())

      localStorage.removeItem("accessToken");
      localStorage.removeItem("memberId");
      localStorage.removeItem("nickname");

      alert("회원 탈퇴가 완료되었습니다");
      navigate("/")
    } catch (err) {
      console.log("회원 탈퇴 실패", err);
      alert("회원 탈퇴에 실패했습니다");
    }
  }

  return (
    <div>
      <form
        onSubmit={handleSubmit}
        className="w-full max-w-lg p-6 rounded shadow bg-white"
      >
        <h2 className="text-xl font-semibold mb-4">회원 정보 수정</h2>

        {/* 비밀번호 */}
        <div className="mb-3">
          <label className="block mb-1">비밀번호</label>
          <input
            type="password"
            name="password"
            value={form.password}
            onChange={handleChange}
            className="w-full border px-3 py-1.5 rounded"
            placeholder="변경할 비밀번호"
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
            placeholder="변경할 비밀번호 확인"
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
          />
        </div>

        {/* 주소 */}
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

        {/* 수정 버튼 */}
        <button
          type="submit"
          disabled={loading}
          className="w-full bg-blue-500 text-white py-2 rounded hover:bg-blue-600 cursor-pointer"
        >
          {loading ? "수정 중..." : "회원 정보 수정"}
        </button>
      </form>

      <button onClick={handleWithdraw}>
        회원탈퇴
      </button>    
    </div>
  );
}
