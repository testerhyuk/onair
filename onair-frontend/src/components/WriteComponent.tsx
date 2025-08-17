// components/WriteComponent.tsx
import { useRef, useState } from "react";
import { createArticleApi, saveArticleSummary } from "../api/articleApi";
import { useNavigate } from "react-router";
import axios from "axios";

const categories = ["정치", "경제", "연예", "세계", "사회", "생활/문화", "IT", "과학"];

const mapCategoryToParam = (cat: string) => {
  switch (cat) {
    case "정치": return "POLITICS";
    case "경제": return "ECONOMIC";
    case "연예": return "ENTERTAINMENT";
    case "세계": return "WORLD";
    case "사회": return "SOCIETY";
    case "생활/문화": return "CULTURE";
    case "IT": return "IT";
    case "과학": return "SCIENCE";
    default: return cat.toUpperCase();
  }
};

const WriteComponent: React.FC = () => {
  const editorRef = useRef<HTMLDivElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const categoryRef = useRef<HTMLSelectElement>(null);
  const titleRef = useRef<HTMLInputElement>(null);
  const navigate = useNavigate();

  const [imageFiles, setImageFiles] = useState<File[]>([]);

  const handleImageInsert = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files || files.length === 0) return;

    setImageFiles(prev => [...prev, ...Array.from(files)]);
    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  const handleSubmit = async () => {
    if (!editorRef.current) return;
    const title = titleRef.current?.value || "";
    const category = categoryRef.current
      ? mapCategoryToParam(categoryRef.current.value)
      : "";
    const contentText = editorRef.current.innerText; // 텍스트만 저장

    try {
      const boardId = "1";
      const userId = localStorage.getItem("memberId") || "memberId";

      // 1️⃣ 아티클 생성
      const articleRes = await createArticleApi.createArticle({
        boardId, userId, title, content: contentText, category
      });
      const articleId = articleRes.data.articleId;

      // 2️⃣ 이미지 업로드
      if (imageFiles.length > 0) {
        const filenames = imageFiles.map(f => f.name);
        const presignedRes = await createArticleApi.getPresignedUrls(filenames);
        const urls: string[] = presignedRes.urls.map(u => u.preSignedUrl);

        await Promise.all(imageFiles.map((file, i) => createArticleApi.uploadImageToS3(urls[i], file)));

        const uploadedImageUrls = urls.map(u => u.split("?")[0]);
        await createArticleApi.saveImageMeta({ articleId, userId, imageUrls: uploadedImageUrls });
      }

      // const aiRes = await axios.post("http://localhost:8000/summarize", { text: contentText });
      // const aiSummary = aiRes.data.summary;
      // await saveArticleSummary(articleId, aiSummary);

      alert("작성 완료!");
      editorRef.current.innerText = "";
      setImageFiles([]);
      navigate("/");
    } catch (error) {
      console.error("작성 실패:", error);
      alert("작성 중 오류가 발생했습니다.");
    }
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
      <select defaultValue="정치" ref={categoryRef} style={{ padding: "5px", fontSize: "16px", marginBottom: "10px" }}>
        {categories.map(cat => <option key={cat} value={cat}>{cat}</option>)}
      </select>

      <input type="text" placeholder="제목" ref={titleRef} style={{ border: "2px solid black", padding: "8px", fontSize: "18px" }} />

      <div
        ref={editorRef}
        contentEditable
        style={{ border: "2px solid black", padding: "10px", minHeight: "300px", width: "100%", overflowY: "auto" }}
      ></div>

      {/* 이미지 미리보기 영역 */}
      <div>
        {imageFiles.map((file, idx) => (
          <div key={file.name + idx} style={{ position: "relative", marginBottom: "10px" }}>
            <img src={URL.createObjectURL(file)} style={{ maxWidth: "100%" }} />
            <button
              onClick={() => setImageFiles(prev => prev.filter(f => f !== file))}
              style={{
                position: "absolute",
                top: 0,
                right: 0,
                background: "red",
                color: "white",
                border: "none",
                cursor: "pointer",
                padding: "2px 6px",
                fontSize: "12px"
              }}
            >
              X
            </button>
          </div>
        ))}
      </div>

      <input type="file" accept="image/*" ref={fileInputRef} onChange={handleImageInsert} multiple />

      <button onClick={handleSubmit} style={{ marginTop: "10px", padding: "10px", background: "blue", color: "white", border: "none", borderRadius: "5px", cursor: "pointer" }}>
        작성 완료
      </button>
    </div>
  );
};

export default WriteComponent;
