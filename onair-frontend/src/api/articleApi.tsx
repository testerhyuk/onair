import axios from "axios"
import axiosInstance from "./axiosInstance"

export const API_SERVER_HOST = 'http://localhost:9032'

const prefix = `${API_SERVER_HOST}/v1/article`

export interface ArticleImage {
  id: number;
  imagesUrl: string;
}

export const getArticleSummary = async (articleId: string): Promise<string> => {
  const token = localStorage.getItem("accessToken");
  if (!token) throw new Error("로그인이 필요합니다");

  try {
    const res = await axios.get(`${API_SERVER_HOST}/v1/article-summary/${articleId}`, {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true,
    });
    return res.data.summary; // { summary: "..." } 형태로 반환
  } catch (err: any) {
    console.error("요약 조회 실패", err);
    return "";
  }
};

export const saveArticleSummary = async (articleId: string, summary: string) => {
  const token = localStorage.getItem("accessToken");
  if (!token) throw new Error("로그인이 필요합니다");

  try {
    await axios.post(
      `${API_SERVER_HOST}/v1/article-summary`,
      { articleId, summary },
      {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        withCredentials: true,
      }
    );
  } catch (err: any) {
    console.error("요약 저장 실패", err);
  }
};

export const getLikedArticlesByUserId = async (userId: string): Promise<Article[]> => {
  const token = localStorage.getItem("accessToken");
  if (!token) throw new Error("로그인이 필요합니다");

  try {
    // 1️⃣ 좋아요한 글 ID만 가져오기
    const res = await axios.get<Article[]>(`${API_SERVER_HOST}/v1/article-like/articles/member/${userId}`, {
      headers: { Authorization: `Bearer ${token}` },
      withCredentials: true,
    });

    const likes: { articleId: string }[] = res.data; // ArticleLikeResponse 배열에서 articleId만 사용

    // 2️⃣ 각 좋아요 글의 실제 게시글 정보 가져오기
    const articlesWithImages = await Promise.all(
      likes.map(async (like) => {
        const article = await getArticlesByUserId(userId); // 원래 getArticlesByUserId 호출
        const matchedArticle = article.find(a => a.articleId === like.articleId);

        const images = await getArticleImages(String(like.articleId));
        return {
          articleId: like.articleId,
          title: matchedArticle?.title ?? "제목 없음",
          imageUrl: images[0] || "/gray-background.png",
        };
      })
    );

    return articlesWithImages;
  } catch (err: any) {
    console.error("좋아요 게시글 API 호출 실패", err);
    return [];
  }
};

export const getArticlesByUserId = async (userId: string): Promise<Article[]> => {
  const token = localStorage.getItem("accessToken");
  if (!token) throw new Error("로그인이 필요합니다");

  const res = await axios.get(`${API_SERVER_HOST}/v1/article/member/${userId}`, {
    headers: { Authorization: `Bearer ${token}` },
    withCredentials: true,
  });

  return res.data as Article[];
};

export const getOne = async (articleId : string) => {
    const res = await axios.get(`${prefix}/${articleId}`)
    
    return res.data
}

export const getAll = async (lastArticleId?: string): Promise<Article[]> => {
    const params: Record<string, string | number> = {
        boardId : "1",
        pageSize : "7"
    };

    if (lastArticleId) {
        params.lastArticleId = lastArticleId;
    }

    const res = await axios.get(`${API_SERVER_HOST}/v1/article/article-list`, { params });
    return res.data;
}

export const getByCategory = async (
  category: string,
  lastArticleId?: string | null
): Promise<Article[]> => {
  const params: Record<string, string | number> = {
    boardId: "1",
    category,
    pageSize: 7,
  };
  if (lastArticleId) {
    params.lastArticleId = lastArticleId;
  }

  const res = await axios.get(`${API_SERVER_HOST}/v1/article/category`, {
    params,
  });
  return res.data;
};

export const getRankingArticles = async (dateStr: string): Promise<Article[]> => {
  const res = await axios.get(`${API_SERVER_HOST}/v1/hot-articles/article/date/${dateStr}`);
  return res.data as Article[];
};

export const searchArticles = async (
  keyword: string,
  lastArticleId?: string | null
): Promise<Article[]> => {
  const params: Record<string, string | number> = {
    boardId: "1",
    keyword,
    pageSize: 7,
  };
  if (lastArticleId) {
    params.lastArticleId = lastArticleId;
  }

  const res = await axios.get(`${prefix}/search`, { params });
  return res.data;
};

export interface PreSignedUrlResponse {
  fileName: string;
  preSignedUrl: string;
}

export interface PreSignedUrlListResponse {
  urls: PreSignedUrlResponse[];
}

export const createArticleApi = {
  createArticle: async (
    data: {
      boardId: string;
      userId: string;
      title: string;
      content: string;
      category: string;
    }
  ) => {
    return axiosInstance.post(`${prefix}`, data);
  },

   getPresignedUrls: async (filenames: string[]): Promise<PreSignedUrlListResponse> => {
  const res = await axios.post<PreSignedUrlListResponse>(
    `${API_SERVER_HOST}/v1/article-images/presigned-urls`,
    filenames,
    {
      headers: {
        Authorization: `Bearer ${localStorage.getItem("accessToken")}`,
      },
      withCredentials: true,
    }
  );
  return res.data; // 이제 TypeScript가 urls가 있는 걸 인식함
},

  uploadImageToS3: async (url: string, file: File) => {
    return axios.put(url, file, { headers: { "Content-Type": file.type } });
  },

  saveImageMeta: async (data: { articleId: string | null; userId: string | number; imageUrls: string[] }) => {
    return axiosInstance.post(`${API_SERVER_HOST}/v1/article-images`, data);
  },
};

/** 조회수 증가 */
export const increaseViewCount = async (articleId: string, userId: string) => {
  const token = localStorage.getItem("accessToken");
  if (!token) return; // 로그인 안 되어 있으면 그냥 반환

  const res = await axios.post(
    `${API_SERVER_HOST}/v1/article-views/articles/${articleId}/users/${userId}/count`,
    null,
    {
      headers: {
        Authorization: `Bearer ${token}`,
      },
      withCredentials: true,
    }
  );
  return res.data as number; // 증가 후 조회수
};

/** 조회수 조회 */
export const getViewCount = async (articleId: string) => {
  const res = await axios.get(`${API_SERVER_HOST}/v1/article-views/articles/${articleId}/count`, { withCredentials: true });
  return res.data as number;
};

/** 좋아요 개수 조회 */
export const getLikeCount = async (articleId: string): Promise<number> => {
  try {
    const res = await axios.get(`${API_SERVER_HOST}/v1/article-like/articles/${articleId}/count`);
    return res.data ?? 0;
  } catch (err: any) {
    if (err.response?.status === 404) return 0;
    throw err;
  }
};

/** 좋아요 */
export const likeArticle = async (articleId: string, userId: string) => {
  const token = localStorage.getItem("accessToken");
  if (!token) throw new Error("로그인이 필요합니다");

  try {
    await axios.post(
      `${API_SERVER_HOST}/v1/article-like/articles/${articleId}/users/${userId}/like`,
      null,
      { headers: { Authorization: `Bearer ${token}` }, withCredentials: true }
    );
  } catch (err: any) {
    throw err;
  }
};

/** 좋아요 취소 */
export const unlikeArticle = async (articleId: string, userId: string) => {
  const token = localStorage.getItem("accessToken");
  if (!token) throw new Error("로그인이 필요합니다");

  try {
    await axios.delete(
      `${API_SERVER_HOST}/v1/article-like/articles/${articleId}/users/${userId}/unlike`,
      { headers: { Authorization: `Bearer ${token}` }, withCredentials: true }
    );
  } catch (err: any) {
    throw err;
  }
};

/** 좋아요 상태 확인 */
export const getArticleLikeStatus = async (articleId: string, userId: string): Promise<boolean> => {
  const token = localStorage.getItem("accessToken");
  if (!token) throw new Error("로그인이 필요합니다");

  try {
    const res = await axios.post(
      `${API_SERVER_HOST}/v1/article-like/articles/${articleId}/users/${userId}/status`,
      null,
      { headers: { Authorization: `Bearer ${token}` }, withCredentials: true }
    );
    // 서버에서 likeStatus boolean 반환
    return res.data?.likeStatus ?? false;
  } catch (err: any) {
    if (err.response?.status === 404) return false; // 데이터 없으면 좋아요 안 함
    throw err;
  }
};

export interface ImageData {
  id: string;   // articleImagesId
  url: string;  // imagesUrl
}

export const getArticleImages = async (articleId: string): Promise<string[]> => {
  const token = localStorage.getItem("accessToken");
  const res = await axios.get(`${API_SERVER_HOST}/v1/article-images/article/${articleId}`, {
    headers: { Authorization: `Bearer ${token}` },
    withCredentials: true,
  });

  return res.data; // string[] 형태로 URL만 반환
};

export const deleteArticle = async (articleId: string) => {
  const token = localStorage.getItem("accessToken");
  if (!token) throw new Error("로그인이 필요합니다");

  // 게시글 삭제 요청
  await axios.delete(`${API_SERVER_HOST}/v1/article/${articleId}`, {
    headers: { Authorization: `Bearer ${token}` },
    withCredentials: true,
  });
};

export const deleteArticleImages = async (imageUrls: string[]) => {
  const token = localStorage.getItem("accessToken");
  if (!token) throw new Error("로그인이 필요합니다");

  await axios.delete(`${API_SERVER_HOST}/v1/article-images`, {
    headers: { Authorization: `Bearer ${token}` },
    data: { imageUrls },
    withCredentials: true,
  });
};

// 글 수정 (제목, 내용, 카테고리)
export const updateArticle = async (
  articleId: number | string,
  data: { title: string; content: string; category: string }
) => {
  const token = localStorage.getItem("accessToken");
  if (!token) throw new Error("로그인이 필요합니다");

  return axios.put(`${prefix}/${articleId}`, data, {
    headers: { Authorization: `Bearer ${token}` },
    withCredentials: true,
  });
};

// 새 이미지 S3 업로드 + presigned URL 가져오기
export const getPresignedUrls = async (filenames: string[]) => {
  const token = localStorage.getItem("accessToken");
  if (!token) throw new Error("로그인이 필요합니다");

  const res = await axios.post(`${API_SERVER_HOST}/v1/article-images/presigned-urls`, filenames, {
    headers: { Authorization: `Bearer ${token}` },
    withCredentials: true,
  });
  return res.data; // { urls: [{ preSignedUrl: string }] }
};

export const uploadImageToS3 = async (url: string, file: File) => {
  return axios.put(url, file, { headers: { "Content-Type": file.type } });
};

// 이미지 DB 업데이트 (새 이미지 + 기존 남아있는 이미지)
export const updateImages = async (data: {
  articleId: number;
  userId: string | number;
  newImageUrls: string[];
  remainingImageIds: string[];
}) => {
  const token = localStorage.getItem("accessToken");
  if (!token) throw new Error("로그인이 필요합니다");

  return axios.put(`${API_SERVER_HOST}/v1/article-images`, data, {
    headers: { Authorization: `Bearer ${token}` },
    withCredentials: true,
  });
};
