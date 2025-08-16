// components/EditComponent.tsx
import { useRef, useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router";
import { 
    createArticleApi,
    getOne,
    getArticleImages, 
    deleteArticleImages,
    updateArticle,
} from "../api/articleApi";

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

type ImageItem = { 
  url?: string; // 기존 이미지 URL
  file?: File;  // 새로 추가한 파일
};

const EditComponent: React.FC = () => {
  const { articleId } = useParams<{ articleId: string }>();
  const editorRef = useRef<HTMLDivElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const categoryRef = useRef<HTMLSelectElement>(null);
  const titleRef = useRef<HTMLInputElement>(null);
  const navigate = useNavigate();

  const [newImages, setNewImages] = useState<ImageItem[]>([]);

  // 기존 글 불러오기
  useEffect(() => {
    if (!articleId) return;

    const fetchArticle = async () => {
      try {
        const article = await getOne(articleId);
        titleRef.current!.value = article.title;
        editorRef.current!.innerText = article.content;
        categoryRef.current!.value = categories.find(cat => mapCategoryToParam(cat) === article.category) || "정치";

        const imgs = await getArticleImages(articleId);
        const initialImages: ImageItem[] = imgs.map(url => ({ url }));
        setNewImages(initialImages);
      } catch (err) {
        console.error("글 불러오기 실패:", err);
        alert("글을 불러오는데 실패했습니다.");
      }
    };

    fetchArticle();
  }, [articleId]);

  // 이미지 선택
  const handleImageInsert = (e: React.ChangeEvent<HTMLInputElement>) => {
    const files = e.target.files;
    if (!files || files.length === 0) return;

    const addedImages = Array.from(files).map(f => ({ file: f }));
    setNewImages(prev => [...prev, ...addedImages]);

    if (fileInputRef.current) fileInputRef.current.value = "";
  };

  const handleSubmit = async () => {
    if (!editorRef.current || !articleId) return;
    const title = titleRef.current?.value || "";
    const category = categoryRef.current ? mapCategoryToParam(categoryRef.current.value) : "";
    const contentText = editorRef.current.innerText;
    const userId = localStorage.getItem("memberId") || "memberId";

    try {
      // 기존 이미지 URL만 남기고 삭제할 이미지 찾기
      const existingUrls = newImages.filter(img => img.url).map(img => img.url!);
      const originalUrls = await getArticleImages(articleId); // 서버에 원래 있던 이미지
      const deletedImages = originalUrls.filter(url => !existingUrls.includes(url));

      if (deletedImages.length > 0) {
        await deleteArticleImages(deletedImages);
      }

      // 새로 추가된 파일 업로드
      const newFiles = newImages.filter(img => img.file).map(img => img.file!);
      if (newFiles.length > 0) {
        const filenames = newFiles.map(f => f.name);
        const presignedRes = await createArticleApi.getPresignedUrls(filenames);
        const urls: string[] = presignedRes.urls.map(u => u.preSignedUrl);

        await Promise.all(newFiles.map((file, i) => createArticleApi.uploadImageToS3(urls[i], file)));

        const uploadedImageUrls = urls.map(u => u.split("?")[0]);
        await createArticleApi.saveImageMeta({ articleId, userId, imageUrls: uploadedImageUrls });
      }

      // 글 업데이트
      await updateArticle(articleId, { title, content: contentText, category });

      alert("수정 완료!");
      navigate(`/read/${articleId}`);
    } catch (err) {
      console.error("수정 실패:", err);
      alert("수정 중 오류가 발생했습니다.");
    }
  };

  return (
    <div style={{ display: "flex", flexDirection: "column", gap: "10px" }}>
      <select defaultValue="정치" ref={categoryRef} style={{ padding: "5px", fontSize: "16px", marginBottom: "10px" }}>
        {categories.map(cat => <option key={cat} value={cat}>{cat}</option>)}
      </select>

      <input type="text" placeholder="제목" ref={titleRef} style={{ border: "2px solid black", padding: "8px", fontSize: "18px" }} />

      <div ref={editorRef} contentEditable style={{ border: "2px solid black", padding: "10px", minHeight: "300px", width: "100%", overflowY: "auto" }}></div>

      {/* 이미지 미리보기 */}
      <div>
        {newImages.map((img, idx) => (
          <div key={idx} style={{ position: "relative", marginBottom: "10px" }}>
            <img src={img.url || (img.file ? URL.createObjectURL(img.file) : "")} style={{ maxWidth: "100%" }} />
            <button
              onClick={() => setNewImages(prev => prev.filter((_, i) => i !== idx))}
              style={{ position: "absolute", top: 0, right: 0, background: "red", color: "white", border: "none", cursor: "pointer", padding: "2px 6px", fontSize: "12px" }}
            >
              X
            </button>
          </div>
        ))}
      </div>

      <input type="file" accept="image/*" ref={fileInputRef} onChange={handleImageInsert} multiple />

      <button onClick={handleSubmit} style={{ marginTop: "10px", padding: "10px", background: "blue", color: "white", border: "none", borderRadius: "5px", cursor: "pointer" }}>
        수정 완료
      </button>
    </div>
  );
};

export default EditComponent;
