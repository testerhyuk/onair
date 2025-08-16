import { useEffect, useState } from "react";
import { useNavigate } from "react-router";
import {
  getOne,
  getArticleImages,
  increaseViewCount,
  getViewCount,
  likeArticle,
  unlikeArticle,
  getLikeCount,
  getArticleLikeStatus,
  deleteArticle,
  deleteArticleImages,
} from "../api/articleApi";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faEye, faHeart as solidHeart } from "@fortawesome/free-solid-svg-icons";
import { faHeart as regularHeart } from "@fortawesome/free-regular-svg-icons";
import CommentComponent from "./CommentComponent";

export interface ImageData {
  id: string;   // React key 용
  url: string;  // 서버에서 받은 이미지 URL
}

function ReadComponent({ articleId }: { articleId: string }) {
  const [content, setContent] = useState("");
  const [title, setTitle] = useState("");
  const [createdAt, setCreatedAt] = useState("");
  const [modifiedAt, setModifiedAt] = useState("");
  const [images, setImages] = useState<ImageData[]>([]);
  const [viewCount, setViewCount] = useState(0);
  const [likeCount, setLikeCount] = useState(0);
  const [liked, setLiked] = useState(false);
  const [loadingLike, setLoadingLike] = useState(true);
  const [userId, setUserId] = useState<string | null>(null);

  const memberId = localStorage.getItem("memberId");
  const token = localStorage.getItem("accessToken");
  const navigate = useNavigate();

  useEffect(() => {
    const fetchArticle = async () => {
      try {
        const data = await getOne(articleId);
        setTitle(data.title);
        setContent(data.content);
        setCreatedAt(data.createdAt);
        setModifiedAt(data.modifiedAt);
        setUserId(String(data.userId));

        const rawImages: string[] = await getArticleImages(articleId);
        const imgList: ImageData[] = rawImages.map((url, index) => ({
          id: String(index),
          url,
        }));
        setImages(imgList);

        const count = Number(await getViewCount(articleId)) || 0;
        setViewCount(count);

        const countLike = Number(await getLikeCount(articleId)) || 0;
        setLikeCount(countLike);

        if (memberId && token) {
          increaseViewCount(articleId, memberId).catch(() =>
            console.warn("조회수 증가 실패")
          );

          const status = await getArticleLikeStatus(articleId, memberId);
          setLiked(status);
        }
      } catch (err) {
        console.error("게시글 로드 실패:", err);
      } finally {
        setLoadingLike(false);
      }
    };

    fetchArticle();
  }, [articleId, memberId, token]);

  const toggleLike = async () => {
    if (loadingLike) return;
    if (!memberId || !token) {
      alert("로그인이 필요합니다");
      return;
    }

    setLiked((prevLiked) => {
      setLikeCount((prevCount) =>
        prevLiked ? Number(prevCount) - 1 : Number(prevCount) + 1
      );
      return !prevLiked;
    });

    try {
      if (liked) {
        await unlikeArticle(articleId, memberId);
      } else {
        await likeArticle(articleId, memberId);
      }
    } catch (err) {
      console.error("좋아요 토글 실패:", err);
      setLiked((prevLiked) => {
        setLikeCount((prevCount) =>
          prevLiked ? Number(prevCount) + 1 : Number(prevCount) - 1
        );
        return prevLiked;
      });
    }
  };

  const handleDelete = async () => {
    if (!userId || memberId !== userId) {
      alert("본인 게시글만 삭제 가능합니다.");
      return;
    }

    if (!window.confirm("게시글을 삭제하시겠습니까?")) return;
    
    try {
    // 1. 이미지 삭제
    const imageUrls = images.map(img => img.url);
    await deleteArticleImages(imageUrls);

    // 2. 게시글 삭제
    await deleteArticle(articleId);

    alert("게시글과 이미지가 삭제되었습니다.");
    navigate("/"); // 목록 페이지로 이동
  } catch (err) {
    console.error("삭제 실패:", err);
    alert("삭제 중 오류가 발생했습니다.");
  }
  };

  const formatDate = (dateStr: string) => {
    const d = new Date(dateStr);
    return d.toLocaleString(undefined, {
      year: "numeric",
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    });
  };

  if (!title) {
    return <div className="text-center py-10 text-gray-500">잘못된 게시글입니다.</div>;
  }

  return (
    <article className="max-w-3xl mx-auto p-6 bg-white rounded shadow-md my-8">
      <header className="mb-4 flex justify-between items-center">
        <h1 className="text-3xl font-bold">{title}</h1>
        <div className="flex items-center gap-3">
          <button
            className={`flex items-center text-red-500 ${loadingLike ? "opacity-50 cursor-not-allowed" : ""}`}
            onClick={toggleLike}
            disabled={loadingLike}
          >
            <FontAwesomeIcon className="cursor-pointer" icon={liked ? solidHeart : regularHeart} />
            <span className="ml-1">{likeCount}</span>
          </button>

          {userId && memberId === userId && (
            <>
              <button
                onClick={() => navigate(`/edit/${articleId}`)}
                className="cursor-pointer px-3 py-1 text-sm bg-blue-500 text-white rounded"
              >
                수정
              </button>
              <button
                onClick={handleDelete}
                className="cursor-pointer px-3 py-1 text-sm bg-red-500 text-white rounded"
              >
                삭제
              </button>
            </>
          )}
        </div>
      </header>

      <div className="flex items-center space-x-4 text-gray-500 text-sm mb-4">
        <span className="text-xs">작성일: {formatDate(createdAt)}</span>
        <span className="text-xs">수정일: {formatDate(modifiedAt)}</span>
        <span className="text-xs flex items-center space-x-1">
          <FontAwesomeIcon icon={faEye} />
          <span>{viewCount}</span>
        </span>
      </div>

      <section className="prose prose-lg whitespace-pre-line mt-4">
        {images.map((img) => (
          <img
            key={img.id}
            src={img.url}
            alt={`image-${img.id}`}
            style={{ maxWidth: "100%", margin: "10px 0" }}
          />
        ))}
        <p>{content}</p>
      </section>
      <CommentComponent articleId={articleId} />
    </article>
  );
}

export default ReadComponent;
