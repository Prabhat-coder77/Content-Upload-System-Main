import * as React from "react";
import { cn } from "../../utils/cn";

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
    label?: string;
    error?: string;
}

const Input = React.forwardRef<HTMLInputElement, InputProps>(
    ({ className, type, label, error, ...props }, ref) => {
        const defaultId = React.useId();
        const id = props.id || defaultId;
        return (
            <div className="w-full space-y-1">
                {label && (
                    <label htmlFor={id} className="block text-sm font-medium text-text-main">
                        {label}
                    </label>
                )}
                <input
                    id={id}
                    type={type}
                    className={cn(
                        "flex h-11 w-full rounded-md border-0 bg-[#eff6ff] text-gray-900 font-medium px-3 py-2 text-sm placeholder:text-gray-500 focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent disabled:cursor-not-allowed disabled:opacity-50 transition-colors",
                        error && "ring-2 ring-danger",
                        className
                    )}
                    ref={ref}
                    {...props}
                />
                {error && <p className="text-xs text-danger mt-1">{error}</p>}
            </div>
        );
    }
);
Input.displayName = "Input";

export { Input };
