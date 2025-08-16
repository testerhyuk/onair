import { faMagnifyingGlass, faUser } from "@fortawesome/free-solid-svg-icons";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { useEffect, useRef, useState } from "react";
import { NavLink, Outlet, useNavigate, useLocation } from "react-router";
import { useSelector, useDispatch } from "react-redux";
import { type RootState } from "../redux/store";
import { logout } from "../redux/authSlice";

export default function BasicLayout() {
  const categories = [
    "전체", "정치", "경제", "사회", "생활/문화", "IT",
    "과학", "세계", "연예", "랭킹",
  ];

  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useDispatch();

  const { memberId, accessToken, role } = useSelector((state: RootState) => state.auth);
  const isLoggedIn = !!memberId && !!accessToken;

  const [menuOpen, setMenuOpen] = useState(false);
  const [menuPosition, setMenuPosition] = useState({ top: 0, left: 0 });
  const userButtonRef = useRef<HTMLButtonElement | null>(null);

  const toggleMenu = () => {
    if (!userButtonRef.current) return;
    const rect = userButtonRef.current.getBoundingClientRect();
    setMenuPosition({ top: rect.bottom + window.scrollY, left: rect.left + window.scrollX });
    setMenuOpen((prev) => !prev);
  };

  const handleMenuClick = (path: string) => {
    navigate(path);
    setMenuOpen(false);
  };

  const handleLogout = () => {
    dispatch(logout());
    setMenuOpen(false);
    localStorage.removeItem("accessToken");
    localStorage.removeItem("memberId");
    localStorage.removeItem("nickname");
    navigate("/");
  };

  // 메뉴 외부 클릭 시 닫기
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (userButtonRef.current && !userButtonRef.current.contains(event.target as Node)) {
        const menuEl = document.getElementById("userDropdownMenu");
        if (menuEl && !menuEl.contains(event.target as Node)) setMenuOpen(false);
      }
    };
    if (menuOpen) document.addEventListener("mousedown", handleClickOutside);
    else document.removeEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [menuOpen]);

  const mapCategoryToParam = (cat: string) => {
    switch (cat) {
      case "전체": return "all";
      case "정치": return "politics";
      case "경제": return "economic";
      case "사회": return "society";
      case "생활/문화": return "culture";
      case "IT": return "it";
      case "과학": return "science";
      case "세계": return "world";
      case "연예": return "entertainment";
      case "랭킹": return "ranking";
      default: return cat.toLowerCase();
    }
  };

  const mapParamToCategory = (param: string) => {
    switch (param) {
      case "all": return "전체";
      case "politics": return "정치";
      case "economic": return "경제";
      case "society": return "사회";
      case "culture": return "생활/문화";
      case "it": return "IT";
      case "science": return "과학";
      case "world": return "세계";
      case "entertainment": return "연예";
      case "ranking": return "랭킹";
      default: return "전체";
    }
  };

  let selectedCategory = "전체";
  if (location.pathname.startsWith("/category/")) {
    const parts = location.pathname.split("/");
    selectedCategory = mapParamToCategory(parts[2] || "");
  } else if (location.pathname === "/") selectedCategory = "전체";

  const scrollerRef = useRef<HTMLUListElement | null>(null);
  const isDraggingRef = useRef(false);
  const startXRef = useRef(0);
  const startScrollLeftRef = useRef(0);
  const movedRef = useRef(false);

  const handleMouseDown = (e: React.MouseEvent) => {
    const el = scrollerRef.current;
    if (!el) return;
    isDraggingRef.current = true;
    startXRef.current = e.clientX;
    startScrollLeftRef.current = el.scrollLeft;
    movedRef.current = false;
    el.style.cursor = "grabbing";
  };

  const handleMouseMove = (e: React.MouseEvent) => {
    const el = scrollerRef.current;
    if (!el || !isDraggingRef.current) return;
    const dx = e.clientX - startXRef.current;
    if (Math.abs(dx) > 5) movedRef.current = true;
    el.scrollLeft = startScrollLeftRef.current - dx;
  };

  const handleMouseUp = () => {
    const el = scrollerRef.current;
    if (!el) return;
    isDraggingRef.current = false;
    el.style.cursor = "grab";
    setTimeout(() => (movedRef.current = false), 0);
  };

  const handleCategoryClick = (e: React.MouseEvent, cat: string) => {
    if (movedRef.current) {
      e.preventDefault();
      e.stopPropagation();
      return;
    }
    navigate(cat === "전체" ? "/" : `/category/${mapCategoryToParam(cat)}`);
  };

  return (
    <div className="min-h-screen bg-gray-100 flex justify-center py-8">
      <div className="w-full max-w-md bg-white rounded shadow overflow-visible">
        <header className="flex items-center justify-between px-4 py-3 border-b border-gray-200">
          <NavLink to="search">
            <button aria-label="검색" className="w-10 h-10 rounded-full flex items-center justify-center cursor-pointer" type="button">
              <FontAwesomeIcon icon={faMagnifyingGlass} />
            </button>
          </NavLink>

          <div className="flex-1 flex justify-center">
            <div className="w-28 h-10 flex items-center justify-center font-medium">
              <NavLink to="/" className={({ isActive }) => `w-28 h-10 flex items-center justify-center font-medium ${isActive ? "opacity-100" : "opacity-80"}`}>
                <img src="/logo.png" alt="logo" />
              </NavLink>
            </div>
          </div>

          <button ref={userButtonRef} aria-label="회원정보" className="w-10 h-10 rounded-full flex items-center justify-center cursor-pointer" type="button" onClick={toggleMenu}>
            <FontAwesomeIcon icon={faUser} />
          </button>

          {menuOpen && (
            <div id="userDropdownMenu" className="fixed w-36 bg-white border border-gray-200 rounded shadow-md z-50" style={{ top: menuPosition.top, left: menuPosition.left }}>
              {isLoggedIn ? (
                <>
                  <button className="w-full text-left px-4 py-2 hover:bg-gray-100 cursor-pointer" onClick={() => handleMenuClick("/mypage")}>회원정보</button>
                  
                  {role === "REPORTER" && (
                    <button className="w-full text-left px-4 py-2 hover:bg-gray-100 cursor-pointer" onClick={() => handleMenuClick("/write")}>기사 작성</button>
                  )}
                  
                  <button className="w-full text-left px-4 py-2 hover:bg-gray-100 cursor-pointer" onClick={handleLogout}>로그아웃</button>
                </>
              ) : (
                <>
                  <button className="w-full text-left px-4 py-2 hover:bg-gray-100 cursor-pointer" onClick={() => handleMenuClick("/login")}>로그인</button>
                  <button className="w-full text-left px-4 py-2 hover:bg-gray-100 cursor-pointer" onClick={() => handleMenuClick("/register")}>회원가입</button>
                </>
              )}
            </div>
          )}
        </header>

        <nav className="border-b border-gray-300">
          <ul ref={scrollerRef} onMouseDown={handleMouseDown} onMouseMove={handleMouseMove} onMouseUp={handleMouseUp} onMouseLeave={handleMouseUp} className="flex space-x-3 px-4 py-3 cursor-grab select-none overflow-x-auto" style={{ userSelect: "none", WebkitUserSelect: "none" }}>
            {categories.map((cat) => (
              <li key={cat} onClick={(e) => handleCategoryClick(e, cat)} className={`flex-shrink-0 px-4 py-1 rounded-md cursor-pointer transition-colors ${selectedCategory === cat ? "bg-gray-300 text-black font-semibold" : "text-gray-700 hover:bg-gray-200"}`}>
                {cat}
              </li>
            ))}
          </ul>
        </nav>

        <main className="p-4">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
