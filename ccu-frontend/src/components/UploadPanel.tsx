import * as React from "react";
import { useState, useRef } from "react";
import { UploadCloud, FileType as FileTypeIcon, X } from "lucide-react";
import { Button } from "./ui/Button";
import { cn } from "../utils/cn";
import { formatBytes } from "../utils/format";
import { apiClient } from "../api/axios";
import { toast } from "react-hot-toast";

const MAX_FILE_SIZE = 50 * 1024 * 1024; // 50MB
const ALLOWED_TYPES = [
    "application/pdf",
    "video/mp4",
    "image/jpeg",
    "image/png"
];

interface UploadPanelProps {
    onUploadSuccess: () => void;
}

export function UploadPanel({ onUploadSuccess }: UploadPanelProps) {
    const [isDragging, setIsDragging] = useState(false);
    const [selectedFile, setSelectedFile] = useState<File | null>(null);
    const [uploadProgress, setUploadProgress] = useState(0);
    const [isUploading, setIsUploading] = useState(false);
    const [validationError, setValidationError] = useState<string | null>(null);
    const fileInputRef = useRef<HTMLInputElement>(null);

    const handleDragOver = (e: React.DragEvent) => {
        e.preventDefault();
        setIsDragging(true);
    };

    const handleDragLeave = (e: React.DragEvent) => {
        e.preventDefault();
        setIsDragging(false);
    };

    const validateFile = (file: File): boolean => {
        setValidationError(null);
        if (!ALLOWED_TYPES.includes(file.type)) {
            const err = "Invalid file type. Only PDF, MP4, JPG, and PNG are allowed.";
            setValidationError(err);
            toast.error(err);
            return false;
        }
        if (file.size > MAX_FILE_SIZE) {
            const err = `File is too large. Max size is ${formatBytes(MAX_FILE_SIZE)}.`;
            setValidationError(err);
            toast.error(err);
            return false;
        }
        return true;
    };

    const handleDrop = (e: React.DragEvent) => {
        e.preventDefault();
        setIsDragging(false);
        if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
            const file = e.dataTransfer.files[0];
            if (validateFile(file)) {
                setSelectedFile(file);
            }
        }
    };

    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files.length > 0) {
            const file = e.target.files[0];
            if (validateFile(file)) {
                setSelectedFile(file);
            }
        }
    };

    const handleUpload = async () => {
        if (!selectedFile) return;

        setIsUploading(true);
        setUploadProgress(0);

        const formData = new FormData();
        formData.append("file", selectedFile);

        try {
            const { data } = await apiClient.post("/api/v1/contents", formData, {
                headers: {
                    "Content-Type": "multipart/form-data",
                },
                onUploadProgress: (progressEvent) => {
                    const percentCompleted = Math.round(
                        (progressEvent.loaded * 100) / (progressEvent.total || selectedFile.size)
                    );
                    setUploadProgress(percentCompleted);
                },
            });

            if (data.success) {
                toast.success("File uploaded successfully!");
                setSelectedFile(null);
                setValidationError(null);
                onUploadSuccess();
            } else {
                const err = data.message || "Failed to upload file";
                setValidationError(err);
                toast.error(err);
            }
        } catch (error: any) {
            const err = error.response?.data?.message || "An error occurred during upload";
            setValidationError(err);
            toast.error(err);
            setSelectedFile(null); // Clear file so they can see the error and retry
        } finally {
            setIsUploading(false);
            setUploadProgress(0);
            if (fileInputRef.current) {
                fileInputRef.current.value = "";
            }
        }
    };

    return (
        <div className="bg-surface rounded-xl border border-border p-6 shadow-sm flex flex-col h-full min-h-[400px]">
            <h2 className="text-lg font-semibold mb-6 tracking-tight">Upload Content</h2>

            {!selectedFile ? (
                <div
                    className={cn(
                        "flex-1 flex flex-col items-center justify-center border-2 border-dashed rounded-xl p-10 transition-all",
                        isDragging ? "border-primary bg-primary/5 scale-[1.02]" : "border-border hover:border-primary/50 hover:bg-white/5",
                        "cursor-pointer"
                    )}
                    onDragOver={handleDragOver}
                    onDragLeave={handleDragLeave}
                    onDrop={handleDrop}
                    onClick={() => fileInputRef.current?.click()}
                >
                    <div className="h-16 w-16 rounded-2xl bg-primary/10 flex items-center justify-center mb-6 text-primary shadow-sm hover:scale-110 transition-transform">
                        <UploadCloud className="h-8 w-8" />
                    </div>
                    <p className="font-semibold text-lg text-center text-text-main mb-1">
                        Drag & drop file here
                    </p>
                    <p className="text-sm text-text-muted text-center mb-6 max-w-xs">
                        Or click to browse from your computer
                    </p>
                    <div className="flex gap-2">
                        <span className="text-xs font-semibold px-2 py-1 bg-white/10 rounded text-text-muted">PDF</span>
                        <span className="text-xs font-semibold px-2 py-1 bg-white/10 rounded text-text-muted">MP4</span>
                        <span className="text-xs font-semibold px-2 py-1 bg-white/10 rounded text-text-muted">JPG</span>
                        <span className="text-xs font-semibold px-2 py-1 bg-white/10 rounded text-text-muted">PNG</span>
                    </div>
                    <p className="text-xs text-text-muted/70 text-center mt-4 font-medium">
                        Max size: {formatBytes(MAX_FILE_SIZE)}
                    </p>
                    {validationError && (
                        <div className="mt-4 px-4 py-2 bg-danger/10 border border-danger/20 rounded-lg text-danger text-sm font-semibold max-w-xs text-center">
                            {validationError}
                        </div>
                    )}
                    <input
                        type="file"
                        ref={fileInputRef}
                        className="hidden"
                        accept=".pdf,.mp4,.jpg,.jpeg,.png"
                        onChange={handleFileChange}
                    />
                </div>
            ) : (
                <div className="flex-1 flex flex-col pt-4">
                    <div className="border border-border rounded-xl p-4 mb-auto bg-white/5">
                        <div className="flex items-start justify-between">
                            <div className="flex items-center gap-4">
                                <div className="h-12 w-12 rounded-xl bg-primary/15 flex items-center justify-center text-primary shrink-0 shadow-sm border border-primary/20">
                                    <FileTypeIcon className="h-6 w-6" />
                                </div>
                                <div className="overflow-hidden">
                                    <p className="font-semibold text-sm text-text-main truncate max-w-[180px]" title={selectedFile.name}>
                                        {selectedFile.name}
                                    </p>
                                    <p className="text-xs text-text-muted font-medium mt-0.5">{formatBytes(selectedFile.size)}</p>
                                </div>
                            </div>
                            <button
                                onClick={() => setSelectedFile(null)}
                                disabled={isUploading}
                                className="text-text-muted hover:text-danger hover:bg-danger/10 rounded-full p-2 transition-colors disabled:opacity-50"
                            >
                                <X className="h-4 w-4" />
                            </button>
                        </div>

                        {isUploading ? (
                            <div className="mt-6">
                                <div className="flex justify-between text-xs mb-2 font-bold tracking-tight">
                                    <span className="text-text-main">Uploading Data...</span>
                                    <span className="text-primary">{uploadProgress}%</span>
                                </div>
                                <div className="w-full bg-border rounded-full h-2 overflow-hidden">
                                    <div
                                        className="bg-primary h-2 rounded-full transition-all duration-300 ease-in-out relative"
                                        style={{ width: `${uploadProgress}%` }}
                                    >
                                        <div className="absolute inset-0 bg-white/20 w-full animate-pulse"></div>
                                    </div>
                                </div>
                            </div>
                        ) : (
                            <div className="mt-6 flex justify-between items-center text-xs font-medium text-success bg-success/10 px-3 py-2 rounded-lg border border-success/20">
                                Ready to upload
                            </div>
                        )}
                    </div>

                    <Button
                        className="w-full mt-6 py-6 font-semibold"
                        onClick={handleUpload}
                        isLoading={isUploading}
                    >
                        {isUploading ? "Uploading..." : "Confirm Upload"}
                    </Button>
                </div>
            )}
        </div>
    );
}
