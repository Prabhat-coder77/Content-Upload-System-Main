
import { useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { UploadPanel } from "../components/UploadPanel";
import { ContentTable } from "../components/ContentTable";
import { apiClient } from "../api/axios";
import { ChevronLeft, ChevronRight } from "lucide-react";
import { Button } from "../components/ui/Button";
import { toast } from "react-hot-toast";

export function Dashboard() {
    const [page, setPage] = useState(0);
    const size = 10;
    const queryClient = useQueryClient();

    const handleDownload = async (id: string, name: string) => {
        try {
            const toastId = toast.loading(`Preparing download for ${name}...`);
            const response = await apiClient.get(`/api/v1/contents/${id}/download`, {
                responseType: 'blob',
            });
            const url = window.URL.createObjectURL(new Blob([response.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', name);
            document.body.appendChild(link);
            link.click();
            link.parentNode?.removeChild(link);
            window.URL.revokeObjectURL(url);
            toast.success("Download started", { id: toastId });
        } catch (error) {
            console.error("Download failed:", error);
            toast.error("Failed to download file");
        }
    };

    const handleDelete = async (id: string) => {
        const toastId = toast.loading("Deleting...");
        try {
            await apiClient.delete(`/api/v1/contents/${id}`);
            toast.success("Content deleted", { id: toastId });
            queryClient.invalidateQueries({ queryKey: ['contents'] });
        } catch (error: any) {
            const msg = error?.response?.data?.message || "Failed to delete content";
            toast.error(msg, { id: toastId });
            throw error;
        }
    };

    const handleRename = async (id: string, newName: string) => {
        const toastId = toast.loading("Renaming...");
        try {
            await apiClient.put(`/api/v1/contents/${id}`, { name: newName });
            toast.success("Content renamed", { id: toastId });
            queryClient.invalidateQueries({ queryKey: ['contents'] });
        } catch (error: any) {
            const msg = error?.response?.data?.message || "Failed to rename content";
            toast.error(msg, { id: toastId });
            throw error;
        }
    };

    const { data, isLoading } = useQuery({
        queryKey: ['contents', page, size],
        queryFn: async () => {
            const response = await apiClient.get('/api/v1/contents', {
                params: { page, size }
            });
            return response.data.data;
        }
    });

    const onUploadSuccess = () => {
        if (page !== 0) {
            setPage(0);
        }
        queryClient.invalidateQueries({ queryKey: ['contents'] });
    };

    const contents = data?.data || [];
    const totalPages = data?.totalPages || 0;

    return (
        <div className="h-full flex flex-col gap-6">
            <div className="flex flex-col md:flex-row justify-between items-start md:items-end gap-4">
                <div>
                    <h1 className="text-3xl font-extrabold text-text-main tracking-tight">Dashboard Overview</h1>
                    <p className="text-text-muted mt-1.5 font-medium">Manage, upload and download your course content</p>
                </div>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 flex-1">
                <div className="lg:col-span-1">
                    <UploadPanel onUploadSuccess={onUploadSuccess} />
                </div>

                <div className="lg:col-span-2 flex flex-col gap-6">
                    <ContentTable
                        contents={contents}
                        isLoading={isLoading}
                        onDownload={handleDownload}
                        onDelete={handleDelete}
                        onRename={handleRename}
                    />

                    {totalPages > 0 && (
                        <div className="flex items-center justify-between mt-auto bg-surface px-5 py-4 rounded-xl border border-border shadow-sm">
                            <div className="text-sm text-text-muted font-bold tracking-wide">
                                PAGE {page + 1} OF {totalPages}
                            </div>
                            <div className="flex items-center gap-3">
                                <Button
                                    variant="outline"
                                    size="sm"
                                    className="font-semibold shadow-sm"
                                    onClick={() => setPage(p => Math.max(0, p - 1))}
                                    disabled={page === 0 || isLoading}
                                >
                                    <ChevronLeft className="h-4 w-4 mr-1" />
                                    Previous
                                </Button>
                                <Button
                                    variant="outline"
                                    size="sm"
                                    className="font-semibold shadow-sm"
                                    onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))}
                                    disabled={page >= totalPages - 1 || isLoading}
                                >
                                    Next
                                    <ChevronRight className="h-4 w-4 ml-1" />
                                </Button>
                            </div>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}
