import { lazy, Suspense } from "react";
import { createBrowserRouter } from "react-router";
import BasicLayout from "../layouts/basicLayout";

const Loading = () => <div>Loading...</div>

const List = lazy(() => import("../pages/listPage"))
const Search = lazy(() => import("../pages/searchPage"))
const Read = lazy(() => import("../pages/readPage"))
const Add = lazy(() => import("../pages/writePage"))
const Edit = lazy(() => import("../pages/editPage"))
const Category = lazy(() => import("../pages/categoryPage"))
const Register = lazy(() => import("../pages/registerPage"))
const Login = lazy(() => import("../pages/loginPage"))
const Write = lazy(() => import("../pages/writePage"))
const MyPage = lazy(() => import("../pages/myPage"))
const Comment = lazy(() => import("../pages/commentPage"))
const EditProfile = lazy(() => import("../pages/editProfilePage"))

const router = createBrowserRouter([
  {
    path: "/",
    Component: BasicLayout,
    children: [
        { 
            index: true,
            element: <Suspense fallback={<Loading />}><List /></Suspense>
        },
        {
            path: "search",
            element: <Suspense fallback={<Loading />}><Search /></Suspense>
        },
        {
            path: "read/:articleId",
            element: <Suspense fallback={<Loading />}><Read /></Suspense>
        },
        {
            path: "add",
            element: <Suspense fallback={<Loading />}><Add /></Suspense>
        },
        {
            path: "edit/:articleId",
            element: <Suspense fallback={<Loading />}><Edit /></Suspense>
        },
        {
            path: "category/:category",
            element: <Suspense fallback={<Loading />}><Category /></Suspense>
        },
        {
            path: "register",
            element: <Suspense fallback={<Loading />}><Register /></Suspense>
        },
        {
            path: "login",
            element: <Suspense fallback={<Loading />}><Login /></Suspense>
        },
        {
            path: "write",
            element: <Suspense fallback={<Loading />}><Write /></Suspense>
        },
        {
            path: "mypage",
            element: <Suspense fallback={<Loading />}><MyPage /></Suspense>
        },
        {
            path: "comment",
            element: <Suspense fallback={<Loading />}><Comment /></Suspense>
        },
        {
            path: "profile/edit",
            element: <Suspense fallback={<Loading />}><EditProfile /></Suspense>
        },
    ]
  },
])

export default router;