import { useEffect, useState, useCallback } from "react";
import { getAll, getArticleImages } from "../api/articleApi";
import { getCommentCount } from "../api/commentApi";
import { useNavigate } from "react-router";

export default function ListComponent() {
  const [articles, setArticles] = useState<Article[]>([]);
  const [articleImages, setArticleImages] = useState<Record<string, string[]>>({});
  const [lastArticleId, setLastArticleId] = useState<string | undefined>(undefined);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [commentCounts, setCommentCounts] = useState<Record<string, number>>({});
  const navigate = useNavigate();

  const loadArticles = useCallback(async () => {
    if (loading || !hasMore) return;
    setLoading(true);

    try {
      const data: Article[] = await getAll(lastArticleId);

      if (data && data.length > 0) {
        const newArticles = data.filter(
          (a) => !articles.some((p) => p.articleId === a.articleId)
        );
        setArticles((prev) => [...prev, ...newArticles]);

        newArticles.forEach(async (article) => {
          const count = await getCommentCount(article.articleId.toString());
          setCommentCounts((prev) => ({ ...prev, [article.articleId]: count }));

          const images = await getArticleImages(article.articleId.toString());
          setArticleImages((prev) => ({ ...prev, [article.articleId]: images }));
        });

        setLastArticleId(data[data.length - 1].articleId.toString());
        if (data.length < 5) setHasMore(false);
      } else {
        setHasMore(false);
      }
    } catch (error) {
      console.error(error);
    }

    setLoading(false);
  }, [lastArticleId, loading, hasMore, articles]);

  useEffect(() => {
    loadArticles();
  }, []);

  useEffect(() => {
    const onScroll = () => {
      if (
        window.innerHeight + window.scrollY >= document.body.offsetHeight - 300 &&
        !loading &&
        hasMore
      ) {
        loadArticles();
      }
    };
    window.addEventListener("scroll", onScroll);
    return () => window.removeEventListener("scroll", onScroll);
  }, [loading, hasMore, loadArticles]);

  return (
    <div className="max-w-3xl mx-auto">
      {articles.map((article, idx) => (
        <div
          key={`${article.articleId}-${idx}`}
          className="flex items-start border-b border-gray-200 py-4 space-x-4 cursor-pointer"
          onClick={() => navigate(`/read/${article.articleId}`)}
        >
          {/* 이미지 영역 */}
          <div className="w-24 h-16 flex-shrink-0 rounded overflow-hidden bg-white">
            {articleImages[article.articleId] &&
            articleImages[article.articleId].length > 0 ? (
              <img
                src={articleImages[article.articleId][0]}
                alt={article.title}
                className="w-full h-full object-cover"
              />
            ) : (
              <div className="w-full h-full bg-gray-100" />
            )}
          </div>

          {/* 텍스트 영역 */}
          <div className="flex-1 min-w-0">
            <h3 className="font-semibold text-base leading-snug text-gray-900 line-clamp-2">
              {article.title}
            </h3>
            <p className="mt-1 text-sm text-gray-600 line-clamp-2 whitespace-pre-wrap">
              {article.content}
            </p>
          </div>

          {/* 댓글 수 */}
          <div className="ml-2 flex-shrink-0 flex items-center">
            <span className="text-blue-600 border border-blue-400 rounded px-2 py-0.5 text-xs font-semibold min-w-[30px] text-center">
              {commentCounts[article.articleId] ?? 0}
            </span>
          </div>
        </div>
      ))}

      {loading && (
        <div className="py-4 text-center text-gray-500">로딩중...</div>
      )}
      {!hasMore && !loading && (
        <div className="py-4 text-center text-gray-400">더 이상 게시글이 없습니다.</div>
      )}
    </div>
  );
}
