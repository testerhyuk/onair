import { useEffect, useState } from "react";
import { getArticleSummary } from "../api/articleApi";

interface SummaryComponentProps {
  articleId: string;
}

const SummaryComponent = ({ articleId }: SummaryComponentProps) => {
  const [summary, setSummary] = useState<string>("");
  const [loading, setLoading] = useState<boolean>(true);

  useEffect(() => {
    let retryCount = 0;
    const maxRetries = 10;
    const intervalMs = 500; // 0.5초 간격

    const fetchSummary = async () => {
      try {
        const fetchedSummary = await getArticleSummary(articleId);

        if (!fetchedSummary && retryCount < maxRetries) {
          retryCount++;
          setTimeout(fetchSummary, intervalMs); // 데이터 없으면 재시도
        } else {
          setSummary(fetchedSummary || ""); // 데이터가 없으면 빈 문자열
          setLoading(false);
        }
      } catch (err) {
        if (retryCount < maxRetries) {
          retryCount++;
          setTimeout(fetchSummary, intervalMs); // 실패 시 재시도
        } else {
          console.error("요약 불러오기 실패:", err);
          setLoading(false);
        }
      }
    };

    if (articleId) {
      fetchSummary();
    }
  }, [articleId]);

  if (loading) return <p className="mt-10 text-gray-500">요약 불러오는 중...</p>;

  return (
    <section className="my-4 p-4 bg-gray-100 rounded">
      <h3 className="font-bold mb-2">AI 요약</h3>
      <p>{summary}</p>
    </section>
  );
};

export default SummaryComponent;
