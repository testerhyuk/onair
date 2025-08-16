export interface ArticleList {
  pageSize: number;
  lastArticleId: string | null;
  articles: Article[];
}